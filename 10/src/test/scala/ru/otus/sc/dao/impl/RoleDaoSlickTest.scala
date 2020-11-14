package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.dao.impl.role.RoleDaoSlick
import ru.otus.sc.dao.{Dao, RoleDaoTest}
import ru.otus.sc.model.Role
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class RoleDaoSlickTest extends RoleDaoTest("RoleDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database       = _
  private var roleDao: Dao[Role] = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")
    roleDao = new RoleDaoSlick(db)
    Await.result(roleDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao: Dao[Role] = {
    roleDao.clean()
    roleDao
  }

  override def destroyDao(): Unit = {
    roleDao.destroy()
  }
}
