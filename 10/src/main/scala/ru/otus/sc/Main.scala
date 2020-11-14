package ru.otus.sc

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import java.security.MessageDigest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory
import ru.otus.sc.dao.impl.author.AuthorDaoSlick
import ru.otus.sc.dao.impl.book.BookDaoSlick
import ru.otus.sc.dao.impl.record.RecordDaoSlick
import ru.otus.sc.dao.impl.role.RoleDaoSlick
import ru.otus.sc.dao.impl.user.UserDaoSlick
import ru.otus.sc.route.impl._
import slick.jdbc.JdbcBackend.Database
import ru.otus.sc.service.impl.ServiceImpl
import sttp.tapir.Endpoint
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.docs.openapi._
import sttp.tapir.swagger.akkahttp.SwaggerAkka
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.Using

object Main {
  def createRoute(pathPrefix: String, profile: String, db: Database)(implicit
      ec: ExecutionContextExecutor
  ): Route = {
    // Создаём DAO
    val roleDao   = new RoleDaoSlick(db)
    val authorDao = new AuthorDaoSlick(db)
    val bookDao   = new BookDaoSlick(db)
    val userDao   = new UserDaoSlick(db)
    val recordDao = new RecordDaoSlick(db)

    if (profile.equals("initial")) {
      // Инициализируем базу
      authorDao.init()
      bookDao.init()
      recordDao.init()
      roleDao.init()
      userDao.init()
    }

    // Создаём Router
    val routers = List(
      new AuthorRouter(pathPrefix, new ServiceImpl(authorDao)),
      new BookRouter(pathPrefix, new ServiceImpl(bookDao)),
      new RecordRouter(pathPrefix, new ServiceImpl(recordDao)),
      new RoleRouter(pathPrefix, new ServiceImpl(roleDao)),
      new UserRouter(pathPrefix, new ServiceImpl(userDao))
    )

    val endpoints: List[Endpoint[_, _, _, _]] = routers.flatMap(_.endpoints)
    val openApiDocs: OpenAPI                  = endpoints.toOpenAPI("Books Library", "1.0.0")
    val openApiYml: String                    = openApiDocs.toYaml

    concat(routers.map(_.route) ::: List(new SwaggerAkka(openApiYml).routes): _*)
  }

  def main(args: Array[String]): Unit = {
    // Читаем конфигурационный файл
    val config = ConfigFactory.load()

    // Адрес и порт для HTTP сервера
    val host: String       = config.getString("http.host")
    val port: Int          = config.getInt("http.port")
    val profile: String    = config.getString("profile")
    val pathPrefix: String = config.getString("pathPrefix")

    implicit val actorSystem: ActorSystem = ActorSystem("system")
    import actorSystem.dispatcher

    // Подключаемся к базе по конфигурации из файла
    Using.resource(Database.forConfig("db")) { db =>
      val bindingFuture = Http()
        .newServerAt(host, port)
        .bind(createRoute(pathPrefix, profile, db))

      println(s"Server online at http://$host:$port/")
      println(s"Docs at: http://$host:$port/docs")
      println("Press any key to exit ...")
      StdIn.readLine() // let it run until user presses return

      bindingFuture
        .flatMap(_.unbind())                      // trigger unbinding from the port
        .onComplete(_ => actorSystem.terminate()) // and shutdown when done
    }
  }

  def md5(input: String): String = {
    val md = MessageDigest.getInstance("MD5")
    md.digest(input.getBytes("UTF-8")).mkString
  }
}
