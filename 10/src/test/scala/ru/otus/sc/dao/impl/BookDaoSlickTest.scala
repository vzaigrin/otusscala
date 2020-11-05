package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.dao.impl.author.AuthorDaoSlick
import ru.otus.sc.dao.impl.book.BookDaoSlick
import ru.otus.sc.dao.impl.record.RecordDaoSlick
import ru.otus.sc.dao.{AuthorDao, BookDao, BookDaoTest, RecordDao}
import ru.otus.sc.model.author.Author
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class BookDaoSlickTest extends BookDaoTest("BookDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database         = _
  private var recordDao: RecordDao = _
  private var authorDao: AuthorDao = _
  private var bookDao: BookDao     = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")

    authorDao = new AuthorDaoSlick(db)
    Await.result(authorDao.init(), Duration.Inf)
    tolstoy = Await.result(authorDao.createAuthor(Author(None, "Лев", "Толстой")), Duration.Inf)
    dostoevsky =
      Await.result(authorDao.createAuthor(Author(None, "Фёдор", "Достоевский")), Duration.Inf)
    gogol = Await.result(authorDao.createAuthor(Author(None, "Николай", "Гоголь")), Duration.Inf)
    pushkin =
      Await.result(authorDao.createAuthor(Author(None, "Александр", "Пушкин")), Duration.Inf)
    lermontov =
      Await.result(authorDao.createAuthor(Author(None, "Михаил", "Лермонтов")), Duration.Inf)

    bookDao = new BookDaoSlick(db)
    Await.result(bookDao.init(), Duration.Inf)

    recordDao = new RecordDaoSlick(db)
    Await.result(recordDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao(): BookDao = {
    bookDao.clean()
    bookDao
  }

  override def destroyDao(): Unit = {
    recordDao.destroy()
    authorDao.destroy()
    bookDao.destroy()
  }
}
