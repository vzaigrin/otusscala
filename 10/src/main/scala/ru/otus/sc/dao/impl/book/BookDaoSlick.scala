package ru.otus.sc.dao.impl.book

import java.util.UUID
import ru.otus.sc.dao.Dao
import ru.otus.sc.dao.impl.Slick.{books, books_to_authors, records}
import ru.otus.sc.model.Book
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{ExecutionContext, Future}

class BookDaoSlick(db: Database)(implicit ec: ExecutionContext) extends Dao[Book] {
  override def init(): Future[Unit] =
    db.run(
      DBIO.seq(
        books_to_authors.schema.createIfNotExists,
        books_to_authors.delete,
        books.schema.createIfNotExists,
        books.delete
      )
    )

  override def clean(): Future[Unit] =
    db.run(
      DBIO.seq(
        books_to_authors.delete,
        books.delete
      )
    )

  override def destroy(): Future[Unit] =
    db.run(
      DBIO.seq(
        books_to_authors.schema.dropIfExists,
        books.schema.dropIfExists
      )
    )

  override def create(entity: Book): Future[Book] = {
    val book      = entity.asInstanceOf[Book]
    val authorsID = book.authors.toSeq
    val newId     = UUID.randomUUID()
    val res = for {
      _ <- (books returning books) += BookRow.fromBook(book).copy(id = Some(newId))
      _ <- books_to_authors ++= authorsID.map(a => (newId, a))
    } yield book.copy(id = Some(newId))

    db.run(res.transactionally)
  }

  override def get(id: UUID): Future[Option[Book]] =
    findByCondition(_.id === id).map(_.headOption)

  override def update(entity: Book): Future[Option[Book]] = {
    val book = entity.asInstanceOf[Book]
    book.id match {
      case Some(bookId) =>
        val updateBook =
          books
            .filter(_.id === bookId)
            .map(b => (b.title, b.published, b.pages))
            .update((book.title, book.published, book.pages))

        val deleteAuthors = books_to_authors.filter(_.bookId === bookId).delete
        val insertAuthors = books_to_authors ++= book.authors.map(bookId -> _)

        val action =
          for {
            b <- books.filter(_.id === bookId).forUpdate.result.headOption
            _ <- b match {
              case None    => DBIO.successful(())
              case Some(_) => updateBook >> deleteAuthors >> insertAuthors
            }
          } yield b.map(_ => book)

        db.run(action.transactionally)

      case None => Future.successful(None)
    }
  }

  override def delete(id: UUID): Future[Option[Book]] = {
    val action =
      for {
        b <- books.filter(_.id === id).forUpdate.result.headOption
        res <- b match {
          case None => DBIO.successful(None)
          case Some(bookRow) =>
            val authorQuery = books_to_authors.filter(_.bookId === id)
            for {
              authorSet <- authorQuery.result
              _         <- authorQuery.delete
              _         <- books.filter(_.id === id).delete
              _         <- records.filter(_.bookId === id).delete
            } yield Some(bookRow.toBook(authorSet.map(_._2).toSet))
        }
      } yield res

    db.run(action.transactionally)
  }

  private def findByCondition(condition: Books => Rep[Boolean]): Future[Seq[Book]] =
    db.run(
      books
        .filter(condition)
        .join(books_to_authors)
        .on(_.id === _.bookId)
        .result
    ).map(_.groupMap(_._1)(_._2).view.map {
      case (book, author) => book.toBook(author.map(_._2).toSet)
    }.toVector)

  def findById(id: UUID): Future[Seq[Book]] = findByCondition(_.id === id)

  override def findByField(field: String, value: String): Future[Seq[Book]] =
    field.toLowerCase match {
      case "title" => findByCondition(_.title === value)
      case "author" =>
        db.run(
          books_to_authors
            .filter(_.authorId === UUID.fromString(value))
            .join(books)
            .on(_.bookId === _.id)
            .result
        ).map(_.groupMap(_._2)(_._1._2).view.map {
          case (book, author) => book.toBook(author.toSet)
        }.toSeq)
      case "published" => findByCondition(_.published === value.toInt)
      case "pages"     => findByCondition(_.pages === value.toInt)
      case _           => Future.successful(Seq())
    }

  override def findAll(): Future[Seq[Book]] = findByCondition(_ => true)
}
