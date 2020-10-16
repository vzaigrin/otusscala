package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.dao.impl.record.RecordDaoSlick
import ru.otus.sc.dao.impl.role.RoleDaoSlick
import ru.otus.sc.dao.impl.user.UserDaoSlick
import ru.otus.sc.dao.{RecordDao, UserDao, UserDaoTest}
import ru.otus.sc.model.role.Role
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class UserDaoSlickTest extends UserDaoTest("UserDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database          = _
  private var recordDao: RecordDao  = _
  private var roleDao: RoleDaoSlick = _
  private var userDao: UserDao      = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")

    roleDao = new RoleDaoSlick(db)
    Await.result(roleDao.init(), Duration.Inf)
    readerRole = Await.result(roleDao.createRole(Role(None, "Reader")), Duration.Inf)
    managerRole = Await.result(roleDao.createRole(Role(None, "Manager")), Duration.Inf)
    adminRole = Await.result(roleDao.createRole(Role(None, "Admin")), Duration.Inf)

    userDao = new UserDaoSlick(db)
    Await.result(userDao.init(), Duration.Inf)

    recordDao = new RecordDaoSlick(db)
    Await.result(recordDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao(): UserDao = {
    Await.result(userDao.clean(), Duration.Inf)
    userDao
  }

  override def destroyDao(): Unit = {
    recordDao.destroy()
    roleDao.destroy()
    userDao.destroy()
  }
}
