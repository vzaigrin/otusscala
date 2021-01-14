package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.dao.impl.record.RecordDaoSlick
import ru.otus.sc.dao.impl.user.UserDaoSlick
import ru.otus.sc.dao.{Dao, UserDaoTest}
import ru.otus.sc.model.{Record, User}
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class UserDaoSlickTest extends UserDaoTest("UserDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database           = _
  private var userDao: Dao[User]     = _
  private var recordDao: Dao[Record] = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")
    recordDao = new RecordDaoSlick(db)
    Await.result(recordDao.init(), Duration.Inf)
    userDao = new UserDaoSlick(db)
    Await.result(userDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao: Dao[User] = {
    Await.result(userDao.clean(), Duration.Inf)
    userDao
  }

  override def destroyDao(): Unit = {
    recordDao.destroy()
    userDao.destroy()
  }
}
