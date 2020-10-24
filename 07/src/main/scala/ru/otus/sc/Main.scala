package ru.otus.sc

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import java.security.MessageDigest
import com.typesafe.config.ConfigFactory
import ru.otus.sc.dao.impl.author.AuthorDaoSlick
import ru.otus.sc.dao.impl.book.BookDaoSlick
import ru.otus.sc.dao.impl.record.RecordDaoSlick
import ru.otus.sc.dao.impl.role.RoleDaoSlick
import ru.otus.sc.dao.impl.user.UserDaoSlick
import ru.otus.sc.dao._
import slick.jdbc.JdbcBackend.Database
import ru.otus.sc.route._
import ru.otus.sc.service.impl._
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.Using

object Main {
  def createRoute(profile: String, db: Database)(implicit ec: ExecutionContextExecutor): Router = {
    // Создаём DAO
    val roleDao: RoleDao     = new RoleDaoSlick(db)
    val authorDao: AuthorDao = new AuthorDaoSlick(db)
    val bookDao: BookDao     = new BookDaoSlick(db)
    val userDao: UserDao     = new UserDaoSlick(db)
    val recordDao: RecordDao = new RecordDaoSlick(db)

    if (profile.equals("initial")) {
      // Инициализируем базу
      roleDao.init()
      authorDao.init()
      bookDao.init()
      userDao.init()
      recordDao.init()
    }

    // Создаём Router
    val authorRoute  = new AuthorRouter(new AuthorServiceImpl(authorDao))
    val roleRouter   = new RoleRouter(new RoleServiceImpl(roleDao))
    val userRouter   = new UserRouter(new UserServiceImpl(userDao))
    val bookRouter   = new BookRouter(new BookServiceImpl(bookDao))
    val recordRouter = new RecordRouter(new RecordServiceImpl(recordDao))

    new Router(authorRoute, bookRouter, roleRouter, userRouter, recordRouter)
  }

  def main(args: Array[String]): Unit = {
    // Читаем конфигурационный файл
    val config = ConfigFactory.load()

    // Адрес и порт для HTTP сервера
    val host: String = config.getString("http.host")
    val port: Int    = config.getInt("http.port")

    implicit val system: ActorSystem = ActorSystem("system")
    import system.dispatcher

    // Подключаемся к базе по конфигурации из файла
    Using.resource(Database.forConfig("db")) { db =>
      val bindingFuture =
        Http().newServerAt(host, port).bind(createRoute(config.getString("profile"), db).route)

      println(s"Server online at http://$host:$port/\nPress RETURN to stop...")
      StdIn.readLine() // let it run until user presses return
      bindingFuture
        .flatMap(_.unbind())                 // trigger unbinding from the port
        .onComplete(_ => system.terminate()) // and shutdown when done
    }
  }

  def md5(input: String): String = {
    val md = MessageDigest.getInstance("MD5")
    md.digest(input.getBytes("UTF-8")).mkString
  }
}
