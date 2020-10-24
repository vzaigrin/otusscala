package ru.otus.sc.dao.impl.record

import java.time.LocalDateTime
import java.util.UUID
import ru.otus.sc.dao.RecordDao
import ru.otus.sc.dao.impl.Slick.{
  authors,
  books,
  books_to_authors,
  records,
  roles,
  users,
  users_to_roles
}
import ru.otus.sc.model.book.Book
import ru.otus.sc.model.record.Record
import ru.otus.sc.model.user.User
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class RecordDaoSlick(db: Database)(implicit ec: ExecutionContext) extends RecordDao {
  override def init(): Future[Unit] =
    db.run(
      DBIO.seq(
        records.schema.createIfNotExists,
        records.delete
      )
    )

  override def clean(): Future[Int] = db.run(records.delete)

  override def destroy(): Future[Unit] = db.run(DBIO.seq(records.schema.dropIfExists))

  override def createRecord(record: Record): Future[Record] = {
    val newId: UUID = UUID.randomUUID()
    val res = for {
      _ <- (records returning records) += RecordRow.fromRecord(record).copy(id = Some(newId))
    } yield record.copy(id = Some(newId))

    db.run(res)
  }

  override def getRecord(id: UUID): Future[Option[Record]] =
    findByCondition(_.id === id).map(_.headOption)

  override def updateRecord(record: Record): Future[Option[Record]] = {
    record.id match {
      case Some(recordId) =>
        val updateRecord =
          records
            .filter(_.id === recordId)
            .map(r => (r.userId, r.bookId, r.getDT, r.returnDT))
            .update((record.user.id.get, record.book.id.get, record.getDT, record.returnDT))
        db.run(updateRecord)
        getRecord(recordId)
      case None => Future.successful(None)
    }
  }

  override def deleteRecord(id: UUID): Future[Option[Record]] = {
    val action =
      for {
        r <- records.filter(_.id === id).forUpdate.result.headOption
        res <- r match {
          case None => DBIO.successful(None)
          case Some(_) =>
            for {
              record <- records.filter(_.id === id).result
              userRoleSet <-
                records
                  .filter(_.id === id)
                  .join(users)
                  .on(_.userId === _.id)
                  .join(users_to_roles)
                  .on { case ((_, user), user2role) => user.id === user2role.userId }
                  .join(roles)
                  .on { case (((_, _), user2role), role) => user2role.roleId === role.id }
                  .result
                  .map(_.groupMap(_._1._1._2)(_._2))
              bookAuthorSet <-
                records
                  .filter(_.id === id)
                  .join(books)
                  .on(_.bookId === _.id)
                  .join(books_to_authors)
                  .on { case ((_, book), book2author) => book.id === book2author.bookId }
                  .join(authors)
                  .on { case (((_, _), book2author), author) => book2author.authorId === author.id }
                  .result
                  .map(_.groupMap(_._1._1._2)(_._2))
              _ <- records.filter(_.id === id).delete
            } yield record
              .zip(userRoleSet.map(u => u._1.toUser(u._2.map(_.toRole).toSet)))
              .zip(bookAuthorSet.map(b => b._1.toBook(b._2.map(_.toAuthor).toSet)))
              .map(rub => rub._1._1.toRecord(rub._1._2, rub._2))
              .headOption
        }
      } yield res

    db.run(action.transactionally)
  }

  private def findByCondition(condition: Records => Rep[Boolean]): Future[Vector[Record]] = {
    val res = for {
      record <- records.filter(condition).result
      userRoleSet <-
        records
          .filter(condition)
          .join(users)
          .on(_.userId === _.id)
          .join(users_to_roles)
          .on { case ((_, user), user2role) => user.id === user2role.userId }
          .join(roles)
          .on { case (((_, _), user2role), role) => user2role.roleId === role.id }
          .result
          .map(_.groupMap(_._1._1._2)(_._2))
      bookAuthorSet <-
        records
          .filter(condition)
          .join(books)
          .on(_.bookId === _.id)
          .join(books_to_authors)
          .on { case ((_, book), book2author) => book.id === book2author.bookId }
          .join(authors)
          .on { case (((_, _), book2author), author) => book2author.authorId === author.id }
          .result
          .map(_.groupMap(_._1._1._2)(_._2))
    } yield record
      .zip(userRoleSet.map(u => u._1.toUser(u._2.map(_.toRole).toSet)))
      .zip(bookAuthorSet.map(b => b._1.toBook(b._2.map(_.toAuthor).toSet)))
      .map(rub => rub._1._1.toRecord(rub._1._2, rub._2))
      .toVector

    db.run(res)
  }

  override def findByUser(user: User): Future[Seq[Record]] =
    findByCondition(_.userId === user.id.get)

  override def findByBook(book: Book): Future[Seq[Record]] =
    findByCondition(_.bookId === book.id.get)

  override def findByGet(dt: LocalDateTime): Future[Seq[Record]] = findByCondition(_.getDT === dt)

  override def findByReturn(dt: LocalDateTime): Future[Seq[Record]] =
    findByCondition(_.returnDT === dt)

  override def findAll(): Future[Seq[Record]] = findByCondition(_ => true)
}
