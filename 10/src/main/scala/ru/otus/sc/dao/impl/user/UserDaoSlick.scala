package ru.otus.sc.dao.impl.user

import java.util.UUID
import ru.otus.sc.dao.Dao
import ru.otus.sc.dao.impl.Slick.{records, users, users_to_roles}
import ru.otus.sc.model.User
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class UserDaoSlick(db: Database)(implicit ec: ExecutionContext) extends Dao[User] {
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

  override def create(entity: User): Future[User] = {
    val user   = entity.asInstanceOf[User]
    val roleID = user.roles.toSeq
    val newId  = UUID.randomUUID()
    val res = for {
      _ <- (users returning users) += UserRow.fromUser(user).copy(id = Some(newId))
      _ <- users_to_roles ++= roleID.map(r => (newId, r))
    } yield user.copy(id = Some(newId))

    db.run(res.transactionally)
  }

  override def get(id: UUID): Future[Option[User]] =
    findByCondition(_.id === id).map(_.headOption)

  override def update(entity: User): Future[Option[User]] = {
    val user = entity.asInstanceOf[User]
    user.id match {
      case Some(userId) =>
        val updateUser =
          users
            .filter(_.id === userId)
            .map(u => (u.userName, u.password, u.firstName, u.lastName, u.age))
            .update((user.userName, user.password, user.firstName, user.lastName, user.age))

        val deleteRoles = users_to_roles.filter(_.userId === userId).delete
        val insertRoles = users_to_roles ++= user.roles.map(userId -> _)

        val action =
          for {
            u <- users.filter(_.id === userId).forUpdate.result.headOption
            _ <- u match {
              case None    => DBIO.successful(())
              case Some(_) => updateUser >> deleteRoles >> insertRoles
            }
          } yield u.map(_ => user)

        db.run(action.transactionally)

      case None => Future.successful(None)
    }
  }

  override def delete(id: UUID): Future[Option[User]] = {
    val action =
      for {
        u <- users.filter(_.id === id).forUpdate.result.headOption
        res <- u match {
          case None => DBIO.successful(None)
          case Some(userRow) =>
            val roleQuery = users_to_roles.filter(_.userId === id)
            for {
              roleSet <- roleQuery.result
              _       <- roleQuery.delete
              _       <- users.filter(_.id === id).delete
              _       <- records.filter(_.userId === id).delete
            } yield Some(userRow.toUser(roleSet.map(_._2).toSet))
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
        .result
    ).map(_.groupMap(_._1)(_._2).view.map {
      case (user, role) => user.toUser(role.map(_._2).toSet)
    }.toVector)

  override def findByField(field: String, value: String): Future[Seq[User]] =
    field.toLowerCase match {
      case "username"  => findByCondition(_.userName === value)
      case "firstname" => findByCondition(_.firstName === value)
      case "lastname"  => findByCondition(_.lastName === value)
      case "age"       => findByCondition(_.age === value.toInt)
      case "role" =>
        db.run(
          users_to_roles
            .filter(_.roleId === UUID.fromString(value))
            .join(users)
            .on(_.userId === _.id)
            .result
        ).map(_.groupMap(_._2)(_._1._2).view.map {
          case (user, role) => user.toUser(role.toSet)
        }.toSeq)
      case _ => Future.successful(Seq())
    }

  override def findAll(): Future[Seq[User]] = findByCondition(_ => true)
}
