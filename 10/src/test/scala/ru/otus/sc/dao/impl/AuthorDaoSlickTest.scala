package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.dao.impl.author.AuthorDaoSlick
import ru.otus.sc.dao.impl.book.BookDaoSlick
import ru.otus.sc.dao.{AuthorDaoTest, Dao}
import ru.otus.sc.model.{Author, Book}
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class AuthorDaoSlickTest extends AuthorDaoTest("AuthorDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database           = _
  private var authorDao: Dao[Author] = _
  private var bookDao: Dao[Book]     = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")
    bookDao = new BookDaoSlick(db)
    Await.result(bookDao.init(), Duration.Inf)
    authorDao = new AuthorDaoSlick(db)
    Await.result(authorDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao: Dao[Author] = {
    authorDao.clean()
    authorDao
  }

  override def destroyDao(): Unit = {
    bookDao.destroy()
    authorDao.destroy()
  }
}
