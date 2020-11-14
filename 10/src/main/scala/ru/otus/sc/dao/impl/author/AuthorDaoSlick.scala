package ru.otus.sc.dao.impl.author

import java.util.UUID
import ru.otus.sc.dao.Dao
import ru.otus.sc.dao.impl.Slick.{authors, books_to_authors}
import ru.otus.sc.model.Author
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class AuthorDaoSlick(db: Database)(implicit ec: ExecutionContext) extends Dao[Author] {
  override def init(): Future[Unit] =
    db.run(
      DBIO.seq(
        authors.schema.createIfNotExists,
        authors.delete
      )
    )

  override def clean(): Future[Unit] = db.run(DBIO.seq(authors.delete))

  override def destroy(): Future[Unit] = db.run(DBIO.seq(authors.schema.dropIfExists))

  override def create(entity: Author): Future[Author] = {
    val author = entity.asInstanceOf[Author]
    val newId  = UUID.randomUUID()
    val res = for {
      _ <- (authors returning authors) += AuthorRow.fromAuthor(author).copy(id = Some(newId))
    } yield author.copy(id = Some(newId))

    db.run(res.transactionally)
  }

  override def get(id: UUID): Future[Option[Author]] = {
    val res = for {
      author <- authors.filter(author => author.id === id).result.headOption
    } yield author.map(_.toAuthor)

    db.run(res)
  }

  override def update(entity: Author): Future[Option[Author]] = {
    val author = entity.asInstanceOf[Author]
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

  override def delete(id: UUID): Future[Option[Author]] = {
    val action =
      for {
        a <- authors.filter(_.id === id).forUpdate.result.headOption
        res <- a match {
          case None => DBIO.successful(None)
          case Some(authorRow) =>
            for {
              _ <- authors.filter(_.id === id).delete
              _ <- books_to_authors.filter(_.authorId === id).delete
            } yield Some(authorRow.toAuthor)
        }
      } yield res

    db.run(action.transactionally)
  }

  private def findByCondition(condition: Authors => Rep[Boolean]): Future[Seq[Author]] =
    db.run(authors.filter(condition).result)
      .map(_.map { authorRow => authorRow.toAuthor }.toSeq)

  override def findByField(field: String, value: String): Future[Seq[Author]] =
    field.toLowerCase match {
      case "firstname" => findByCondition(_.firstName === value)
      case "lastname"  => findByCondition(_.lastName === value)
      case _           => Future.successful(Seq())
    }

  override def findAll(): Future[Seq[Author]] = findByCondition(_ => true)
}
