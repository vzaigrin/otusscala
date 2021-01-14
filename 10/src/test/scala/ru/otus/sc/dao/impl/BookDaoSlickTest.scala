package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.dao.impl.book.BookDaoSlick
import ru.otus.sc.dao.impl.record.RecordDaoSlick
import ru.otus.sc.dao.{BookDaoTest, Dao}
import ru.otus.sc.model.{Book, Record}
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class BookDaoSlickTest extends BookDaoTest("BookDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database           = _
  private var bookDao: Dao[Book]     = _
  private var recordDao: Dao[Record] = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")
    recordDao = new RecordDaoSlick(db)
    Await.result(recordDao.init(), Duration.Inf)
    bookDao = new BookDaoSlick(db)
    Await.result(bookDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao: Dao[Book] = {
    bookDao.clean()
    bookDao
  }

  override def destroyDao(): Unit = {
    recordDao.destroy()
    bookDao.destroy()
  }
}
