package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.dao.impl.role.{RoleDaoSlick, Roles}
import ru.otus.sc.dao.{RoleDao, RoleDaoTest}
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class RoleDaoSlickTest extends RoleDaoTest("RoleDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database     = _
  private var roleDao: RoleDao = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")
    roleDao = new RoleDaoSlick(db)
    Await.result(roleDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao(): RoleDao = {
    roleDao.clean()
    roleDao
  }

  override def destroyDao(): Unit = {
    roleDao.destroy()
  }
}
