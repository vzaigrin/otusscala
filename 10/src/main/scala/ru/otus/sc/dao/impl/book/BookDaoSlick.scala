package ru.otus.sc.dao.impl.book

import java.util.UUID

import ru.otus.sc.dao.BookDao
import ru.otus.sc.dao.impl.Slick.{authors, books, books_to_authors, records}
import ru.otus.sc.model.author.Author
import ru.otus.sc.model.book.Book
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class BookDaoSlick(db: Database)(implicit ec: ExecutionContext) extends BookDao {
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

  override def createBook(book: Book): Future[Book] = {
    val authorsID: Seq[UUID] = book.authors.map(_.id.get).toSeq
    val newId: UUID          = UUID.randomUUID()
    val res = for {
      _ <- (books returning books) += BookRow.fromBook(book).copy(id = Some(newId))
      _ <- books_to_authors ++= authorsID.map(a => (newId, a))
    } yield book.copy(id = Some(newId))

    db.run(res.transactionally)
  }

  override def getBook(id: UUID): Future[Option[Book]] =
    findByCondition(_.id === id).map(_.headOption)
//  override def getBook(id: UUID): Future[Option[Book]] = {
//    val res = for {
//      book <- books.filter(book => book.id === id).result.headOption
//      authorSet <- (books_to_authors.filter(ba => ba.bookId === id) join authors on (_.authorId === _.id))
//        .map(_._2)
//        .result
//    } yield book.map(_.toBook(authorSet.map(_.toAuthor).toSet))
//
//    db.run(res)
//  }

  override def updateBook(book: Book): Future[Option[Book]] =
    book.id match {
      case Some(bookId) =>
        val updateBook =
          books
            .filter(_.id === bookId)
            .map(b => (b.title, b.published, b.pages))
            .update((book.title, book.published, book.pages))

        val deleteAuthors = books_to_authors.filter(_.bookId === bookId).delete
        val insertAuthors = books_to_authors ++= book.authors.map(bookId -> _.id.get)

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

  override def deleteBook(id: UUID): Future[Option[Book]] = {
    val action =
      for {
        b <- books.filter(_.id === id).forUpdate.result.headOption
        res <- b match {
          case None => DBIO.successful(None)
          case Some(bookRow) =>
            val authorQuery = books_to_authors.filter(_.bookId === id)
            for {
              authorSet <-
                books_to_authors
                  .filter(ba => ba.bookId === id)
                  .join(authors)
                  .on(_.authorId === _.id)
                  .map(_._2)
                  .result
              _ <- authorQuery.delete
              _ <- books.filter(_.id === id).delete
              _ <- records.filter(_.bookId === id).delete
            } yield Some(bookRow.toBook(authorSet.map(_.toAuthor).toSet))
        }
      } yield res

    db.run(action.transactionally)
  }

  private def findByCondition(condition: Books => Rep[Boolean]): Future[Vector[Book]] =
    db.run(
      books
        .filter(condition)
        .join(books_to_authors)
        .on(_.id === _.bookId)
        .join(authors)
        .on(_._2.authorId === _.id)
        .result
    ).map(_.groupMap(_._1._1)(_._2).view.map {
      case (book, author) => book.toBook(author.map(_.toAuthor).toSet)
    }.toVector)

  def findById(id: UUID): Future[Seq[Book]] = findByCondition(_.id === id)

  override def findByTitle(title: String): Future[Seq[Book]] = findByCondition(_.title === title)

  override def findByAuthor(author: Author): Future[Seq[Book]] = {
    val authorId: UUID = author.id.get
    db.run(
      books_to_authors
        .filter(_.authorId === authorId)
        .join(books)
        .on(_.bookId === _.id)
        .join(authors)
        .on(_._1.authorId === _.id)
        .result
    ).map(_.groupMap(_._1._2)(_._2).view.map {
      case (book, author) => book.toBook(author.map(_.toAuthor).toSet)
    }.toVector)
  }

  override def findByYear(year: Int): Future[Seq[Book]] = findByCondition(_.published === year)

  override def findByPages(pages: Int): Future[Seq[Book]] = findByCondition(_.pages === pages)

  override def findAll(): Future[Seq[Book]] = findByCondition(_ => true)
}
