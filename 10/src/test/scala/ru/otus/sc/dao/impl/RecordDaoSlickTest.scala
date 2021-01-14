package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.dao.impl.record.RecordDaoSlick
import ru.otus.sc.dao.{Dao, RecordDaoTest}
import ru.otus.sc.model.Record
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class RecordDaoSlickTest extends RecordDaoTest("RecordDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database           = _
  private var recordDao: Dao[Record] = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")
    recordDao = new RecordDaoSlick(db)
    Await.result(recordDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao: Dao[Record] = {
    Await.result(recordDao.clean(), Duration.Inf)
    recordDao
  }

  override def destroyDao(): Unit = {
    recordDao.destroy()
  }
}
