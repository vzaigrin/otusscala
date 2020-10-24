package ru.otus.sc.dao.impl.author

import java.util.UUID

import ru.otus.sc.dao.AuthorDao
import ru.otus.sc.dao.impl.Slick.authors
import ru.otus.sc.model.author.Author
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class AuthorDaoSlick(db: Database)(implicit ec: ExecutionContext) extends AuthorDao {

  override def init(): Future[Unit] =
    db.run(
      DBIO.seq(
        authors.schema.createIfNotExists,
        authors.delete
      )
    )

  override def clean(): Future[Unit] = db.run(DBIO.seq(authors.delete))

  override def destroy(): Future[Unit] = db.run(DBIO.seq(authors.schema.dropIfExists))

  override def createAuthor(author: Author): Future[Author] = {
    val newId = UUID.randomUUID()
    val res = for {
      _ <- (authors returning authors) += AuthorRow.fromAuthor(author).copy(id = Some(newId))
    } yield author.copy(id = Some(newId))

    db.run(res.transactionally)
  }

  override def getAuthor(id: UUID): Future[Option[Author]] = {
    val res = for {
      author <- authors.filter(author => author.id === id).result.headOption
    } yield author.map(_.toAuthor)

    db.run(res)
  }

  override def updateAuthor(author: Author): Future[Option[Author]] = {
    author.id match {
      case Some(authorId) =>
        val updateAuthor =
          authors
            .filter(_.id === authorId)
            .map(a => (a.firstName, a.lastName))
            .update((author.firstName, author.lastName))

        val action =
          for {
            a <- authors.filter(_.id === authorId).forUpdate.result.headOption
            _ <- a match {
              case None    => DBIO.successful(())
              case Some(_) => updateAuthor
            }
          } yield a.map(_ => author)

        db.run(action.transactionally)

      case None => Future.successful(None)
    }
  }

  override def deleteAuthor(id: UUID): Future[Option[Author]] = {
    val action =
      for {
        u <- authors.filter(_.id === id).forUpdate.result.headOption
        res <- u match {
          case None => DBIO.successful(None)
          case Some(authorRow) =>
            for {
              _ <- authors.filter(_.id === id).delete
            } yield Some(authorRow.toAuthor)
        }
      } yield res

    db.run(action.transactionally)
  }

  private def findByCondition(condition: Authors => Rep[Boolean]): Future[Seq[Author]] =
    db.run(authors.filter(condition).result)
      .map(_.map { authorRow => authorRow.toAuthor }.toSeq)

  override def findByLastName(lastName: String): Future[Seq[Author]] =
    findByCondition(_.lastName === lastName)

  override def findAll(): Future[Seq[Author]] = findByCondition(_ => true)
}
