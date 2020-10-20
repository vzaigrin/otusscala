package ru.otus.sc.dao.impl.user

import java.util.UUID

import ru.otus.sc.dao.UserDao
import ru.otus.sc.dao.impl.Slick.{records, roles, users, users_to_roles}
import ru.otus.sc.model.role.Role
import ru.otus.sc.model.user.User
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserDaoSlick(db: Database)(implicit ec: ExecutionContext) extends UserDao {
  override def init(): Future[Unit] =
    db.run(
      DBIO.seq(
        users.schema.createIfNotExists,
        users.delete,
        users_to_roles.schema.createIfNotExists,
        users_to_roles.delete
      )
    )

  override def clean(): Future[Unit] =
    db.run(
      DBIO.seq(
        users_to_roles.delete,
        users.delete
      )
    )

  override def destroy(): Future[Unit] =
    db.run(
      DBIO.seq(
        users_to_roles.schema.dropIfExists,
        users.schema.dropIfExists
      )
    )

  override def createUser(user: User): Future[User] = {
    val roleID: Seq[UUID] = user.roles.map(_.id.get).toSeq
    val newId: UUID       = UUID.randomUUID()
    val res = for {
      _ <- (users returning users) += UserRow.fromUser(user).copy(id = Some(newId))
      _ <- users_to_roles ++= roleID.map(r => (newId, r))
    } yield user.copy(id = Some(newId))

    db.run(res.transactionally)
  }

  override def getUser(id: UUID): Future[Option[User]] =
    findByCondition(_.id === id).map(_.headOption)
//  override def getUser(id: UUID): Future[Option[User]] = {
//    val res = for {
//      user <- users.filter(user => user.id === id).result.headOption
//      roleSet <- (users_to_roles.filter(ur => ur.userId === id) join roles on (_.roleId === _.id))
//        .map(_._2)
//        .result
//    } yield user.map(_.toUser(roleSet.map(_.toRole).toSet))
//
//    db.run(res)
//  }

  override def updateUser(user: User): Future[Option[User]] =
    user.id match {
      case Some(userId) =>
        val updateUser =
          users
            .filter(_.id === userId)
            .map(u => (u.userName, u.password, u.firstName, u.lastName, u.age))
            .update((user.userName, user.password, user.firstName, user.lastName, user.age))

        val deleteRoles = users_to_roles.filter(_.userId === userId).delete
        val insertRoles = users_to_roles ++= user.roles.map(userId -> _.id.get)

        val action =
          for {
            b <- users.filter(_.id === userId).forUpdate.result.headOption
            _ <- b match {
              case None    => DBIO.successful(())
              case Some(_) => updateUser >> deleteRoles >> insertRoles
            }
          } yield b.map(_ => user)

        db.run(action.transactionally)

      case None => Future.successful(None)
    }

  override def deleteUser(id: UUID): Future[Option[User]] = {
    val action =
      for {
        u <- users.filter(_.id === id).forUpdate.result.headOption
        res <- u match {
          case None => DBIO.successful(None)
          case Some(userRow) =>
            val roleQuery = users_to_roles.filter(_.userId === id)
            for {
              roleSet <-
                users_to_roles
                  .filter(ba => ba.userId === id)
                  .join(roles)
                  .on(_.roleId === _.id)
                  .map(_._2)
                  .result
              _ <- roleQuery.delete
              _ <- users.filter(_.id === id).delete
              _ <- records.filter(_.userId === id).delete
            } yield Some(userRow.toUser(roleSet.map(_.toRole).toSet))
        }
      } yield res

    db.run(action.transactionally)
  }

  private def findByCondition(condition: Users => Rep[Boolean]): Future[Vector[User]] =
    db.run(
      users
        .filter(condition)
        .join(users_to_roles)
        .on(_.id === _.userId)
        .join(roles)
        .on(_._2.roleId === _.id)
        .result
    ).map(_.groupMap(_._1._1)(_._2).view.map {
      case (user, role) => user.toUser(role.map(_.toRole).toSet)
    }.toVector)

  override def findByRole(role: Role): Future[Seq[User]] = {
    val roleId: UUID = role.id.get
    db.run(
      users_to_roles
        .filter(_.roleId === roleId)
        .join(users)
        .on(_.userId === _.id)
        .join(roles)
        .on(_._1.roleId === _.id)
        .result
    ).map(_.groupMap(_._1._2)(_._2).view.map {
      case (user, role) => user.toUser(role.map(_.toRole).toSet)
    }.toVector)
  }

  override def findByLastName(lastName: String): Future[Seq[User]] =
    findByCondition(_.lastName === lastName)

  override def findByUserName(userName: String): Future[Seq[User]] =
    findByCondition(_.userName === userName)

  override def findAll(): Future[Seq[User]] = findByCondition(_ => true)
}
