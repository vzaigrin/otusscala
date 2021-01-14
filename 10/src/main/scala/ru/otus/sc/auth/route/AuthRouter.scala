package ru.otus.sc.auth.route

import akka.actor.typed.{ActorSystem, Scheduler}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.http.scaladsl.server.Directives.concat
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import ru.otus.sc.auth.model._
import ru.otus.sc.route.{BadRequest, ErrorInfo, Unauthorized}
import sttp.tapir.{Endpoint, endpoint}
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.akkahttp.RichAkkaHttpEndpoint
import io.circe.generic.auto._
import sttp.tapir.TapirAuth.bearer
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class AuthRouter(pathPrefix: String, authSystem: ActorSystem[AuthRequest]) {
  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  implicit val timeout: Timeout             = 10.seconds
  implicit val scheduler: Scheduler         = authSystem.scheduler

  def endpoints: List[Endpoint[_, _, _, _]] = List(loginEndpoint, logoutEndpoint)
  def route: Route                          = concat(loginRoute, logoutRoute)

  private val startEndpoint: Endpoint[Unit, Unit, Unit, Any] = endpoint.in(pathPrefix)
  private var result: Future[Either[ErrorInfo, AuthToken]]   = _

  def loginEndpoint: Endpoint[AuthUser, ErrorInfo, AuthToken, Any] =
    startEndpoint.post
      .description("Login")
      .tag("login")
      .in("login")
      .errorOut(jsonBody[ErrorInfo])
      .in(jsonBody[AuthUser])
      .out(jsonBody[AuthToken])

  def login(u: AuthUser): Future[Either[ErrorInfo, AuthToken]] = {
    authSystem
      .ask(ref => CheckUser(u.username, u.password, ref))
      .onComplete {
        case Success(UserValid(token))      => result = Future(Right(AuthToken(token)))
        case Success(UserInvalid(username)) => result = Future(Left(Unauthorized(username)))
        case Failure(exception)             => result = Future(Left(BadRequest(exception.toString)))
        case _                              => result = Future(Left(BadRequest(u.toString)))
      }
    result
  }

  def loginRoute: Route = loginEndpoint.toRoute(login)

  def logoutEndpoint: Endpoint[String, ErrorInfo, AuthToken, Any] =
    startEndpoint.post
      .description("Logout")
      .tag("logout")
      .in("logout")
      .in(bearer[String])
      .errorOut(jsonBody[ErrorInfo])
      .out(jsonBody[AuthToken])

  def logout(token: String): Future[Either[ErrorInfo, AuthToken]] = {
    authSystem
      .ask(ref => Logout(token, ref))
      .onComplete {
        case Success(LogoutSuccessful(_))       => result = Future(Right(AuthToken("")))
        case Success(LogoutUnsuccessful(token)) => result = Future(Left(Unauthorized(token)))
        case Failure(exception)                 => result = Future(Left(BadRequest(exception.toString)))
        case _                                  => result = Future(Left(BadRequest(token)))
      }
    result
  }

  def logoutRoute: Route = logoutEndpoint.toRoute(logout)
}
