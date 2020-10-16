package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.dao.impl.author.AuthorDaoSlick
import ru.otus.sc.dao.{AuthorDao, AuthorDaoTest}
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class AuthorDaoSlickTest extends AuthorDaoTest("AuthorDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database         = _
  private var authorDao: AuthorDao = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")
    authorDao = new AuthorDaoSlick(db)
    Await.result(authorDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao(): AuthorDao = {
    authorDao.clean()
    authorDao
  }

  override def destroyDao(): Unit = {
    authorDao.destroy()
  }
}
