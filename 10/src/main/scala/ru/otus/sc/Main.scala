package ru.otus.sc

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import java.security.MessageDigest
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory
import ru.otus.sc.auth.model.AuthRequest
import ru.otus.sc.auth.route.AuthRouter
import ru.otus.sc.auth.service.AuthService
import ru.otus.sc.dao.impl.author.AuthorDaoSlick
import ru.otus.sc.dao.impl.book.BookDaoSlick
import ru.otus.sc.dao.impl.record.RecordDaoSlick
import ru.otus.sc.dao.impl.role.RoleDaoSlick
import ru.otus.sc.dao.impl.user.UserDaoSlick
import ru.otus.sc.model.{Role, User}
import ru.otus.sc.route.impl._
import slick.jdbc.JdbcBackend.Database
import ru.otus.sc.service.impl.ServiceImpl
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.docs.openapi._
import sttp.tapir.swagger.akkahttp.SwaggerAkka
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.Using

object Main {
  def main(args: Array[String]): Unit = {
    // Читаем конфигурационный файл
    val config = ConfigFactory.load()

    // Адрес и порт для HTTP сервера
    val host: String       = config.getString("http.host")
    val port: Int          = config.getInt("http.port")
    val profile: String    = config.getString("profile")
    val pathPrefix: String = config.getString("pathPrefix")

    // Подключаемся к базе по конфигурации из файла
    Using.resource(Database.forConfig("db")) { db =>
      implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "ActorSystem")
      implicit val ec: ExecutionContextExecutor      = actorSystem.executionContext

      // Создаём DAO
      val roleDao   = new RoleDaoSlick(db)
      val authorDao = new AuthorDaoSlick(db)
      val bookDao   = new BookDaoSlick(db)
      val userDao   = new UserDaoSlick(db)
      val recordDao = new RecordDaoSlick(db)

      // Инициализируем базу
      if (profile.equals("initial")) {
        // Очищаем базы
        authorDao.init()
        bookDao.init()
        recordDao.init()
        roleDao.init()
        userDao.init()
        // Создаём роль Admin и пользователя Admin
        roleDao.create(Role(None, "Admin")).onComplete { role =>
          userDao.create(
            User(None, "admin", md5("admin"), "Семен", "Иванов", 30, Set(role.get.id.get))
          )
        }
      }

      val authActor: ActorSystem[AuthRequest] = ActorSystem(AuthService(userDao), "Auth")
      val authRouter: AuthRouter              = new AuthRouter(pathPrefix, authActor)

      // Создаём Routers
      val routers = List(
        new AuthorRouter(pathPrefix, new ServiceImpl(authorDao), authActor),
        new BookRouter(pathPrefix, new ServiceImpl(bookDao), authActor),
        new RecordRouter(pathPrefix, new ServiceImpl(recordDao), authActor),
        new RoleRouter(pathPrefix, new ServiceImpl(roleDao), authActor),
        new UserRouter(pathPrefix, new ServiceImpl(userDao), authActor)
      )

      val endpoints            = routers.flatMap(_.endpoints) ++ authRouter.endpoints
      val openApiDocs: OpenAPI = endpoints.toOpenAPI("Books Library", "1.0.0")
      val openApiYml: String   = openApiDocs.toYaml

      val route: Route =
        concat(
          routers.map(_.route) ::: List(authRouter.route, new SwaggerAkka(openApiYml).routes): _*
        )

      val bindingFuture = Http()
        .newServerAt(host, port)
        .bind(route)

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
    MessageDigest
      .getInstance("MD5")
      .digest(input.getBytes("UTF-8"))
      .map("%02x".format(_))
      .mkString
  }
}
