package ru.otus.sc.dao.impl

import org.scalatest.BeforeAndAfterAll
import ru.otus.sc.Main.md5
import ru.otus.sc.dao.impl.author.AuthorDaoSlick
import ru.otus.sc.dao.impl.book.BookDaoSlick
import ru.otus.sc.dao.impl.record.RecordDaoSlick
import ru.otus.sc.dao.impl.role.RoleDaoSlick
import ru.otus.sc.dao.impl.user.UserDaoSlick
import ru.otus.sc.dao.{AuthorDao, BookDao, RecordDao, RecordDaoTest, RoleDao, UserDao}
import ru.otus.sc.model.author.Author
import ru.otus.sc.model.book.Book
import ru.otus.sc.model.role.Role
import ru.otus.sc.model.user.User
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class RecordDaoSlickTest extends RecordDaoTest("RecordDaoSlickTest") with BeforeAndAfterAll {
  private var db: Database          = _
  private var roleDao: RoleDaoSlick = _
  private var userDao: UserDao      = _
  private var authorDao: AuthorDao  = _
  private var bookDao: BookDao      = _
  private var recordDao: RecordDao  = _

  override def beforeAll(): Unit = {
    db = Database.forConfig("testdb")

    roleDao = new RoleDaoSlick(db)
    Await.result(roleDao.init(), Duration.Inf)
    val readerRole: Role  = Await.result(roleDao.createRole(Role(None, "Reader")), Duration.Inf)
    val managerRole: Role = Await.result(roleDao.createRole(Role(None, "Manager")), Duration.Inf)
    val adminRole: Role   = Await.result(roleDao.createRole(Role(None, "Admin")), Duration.Inf)

    userDao = new UserDaoSlick(db)
    Await.result(userDao.init(), Duration.Inf)
    reader = Await.result(
      userDao.createUser(
        User(None, "reader", md5("reader"), "Иван", "Петров", 50, Set(readerRole))
      ),
      Duration.Inf
    )
    manager = Await.result(
      userDao.createUser(
        User(None, "manager", md5("manager"), "Петр", "Семенов", 20, Set(managerRole))
      ),
      Duration.Inf
    )
    admin = Await.result(
      userDao.createUser(
        User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(adminRole, readerRole))
      ),
      Duration.Inf
    )

    authorDao = new AuthorDaoSlick(db)
    Await.result(authorDao.init(), Duration.Inf)
    val tolstoy: Author =
      Await.result(authorDao.createAuthor(Author(None, "Лев", "Толстой")), Duration.Inf)
    val dostoevsky: Author =
      Await.result(authorDao.createAuthor(Author(None, "Фёдор", "Достоевский")), Duration.Inf)
    val gogol: Author =
      Await.result(authorDao.createAuthor(Author(None, "Николай", "Гоголь")), Duration.Inf)
    val pushkin: Author =
      Await.result(authorDao.createAuthor(Author(None, "Александр", "Пушкин")), Duration.Inf)
    val lermontov: Author =
      Await.result(authorDao.createAuthor(Author(None, "Михаил", "Лермонтов")), Duration.Inf)

    bookDao = new BookDaoSlick(db)
    Await.result(bookDao.init(), Duration.Inf)
    book1 = Await.result(
      bookDao.createBook(Book(None, "Великие романы", Set(tolstoy, dostoevsky), 1914, 1300)),
      Duration.Inf
    )
    book2 = Await.result(
      bookDao.createBook(Book(None, "Великие поэмы", Set(pushkin, lermontov), 1850, 600)),
      Duration.Inf
    )
    book3 = Await.result(
      bookDao.createBook(Book(None, "Вечера на хуторе близ Диканьки", Set(gogol), 1832, 320)),
      Duration.Inf
    )

    recordDao = new RecordDaoSlick(db)
    Await.result(recordDao.init(), Duration.Inf)
  }

  override def afterAll(): Unit = {
    destroyDao()
    db.close
  }

  override def getDao(): RecordDao = {
    Await.result(recordDao.clean(), Duration.Inf)
    recordDao
  }

  override def destroyDao(): Unit = {
    roleDao.destroy()
    userDao.destroy()
    authorDao.destroy()
    bookDao.destroy()
    recordDao.destroy()
  }
}
