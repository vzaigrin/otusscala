package ru.otus.sc.auth.service

import java.time.Instant
import akka.actor.typed.scaladsl.Behaviors
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import ru.otus.sc.Main.md5
import ru.otus.sc.auth.model._
import ru.otus.sc.dao.Dao
import ru.otus.sc.model.User
import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object AuthService {
  private val key                 = "secretKey"
  private val algo                = JwtAlgorithm.HS256
  private var tokens: Set[String] = Set()

  def apply(dao: Dao[User])(implicit ec: ExecutionContextExecutor): Behaviors.Receive[AuthRequest] =
    Behaviors.receiveMessage[AuthRequest] {
      case CheckUser(username, password, replyTo) =>
        dao.findByField("username", username).onComplete {
          case Success(users) =>
            if (users.map(u => (u.userName, u.password)).contains((username, md5(password)))) {
              val now = Instant.now.getEpochSecond
              val claim = JwtClaim(
                expiration = Some(Instant.now.plusSeconds(8 * 60 * 60).getEpochSecond),
                notBefore = Some(now),
                issuedAt = Some(now)
              )
              val token = JwtCirce.encode(claim, key, algo)
              tokens += token
              replyTo ! UserValid(token)
            } else {
              replyTo ! UserInvalid(username)
            }
          case Failure(_) =>
            replyTo ! UserInvalid(username)
        }
        Behaviors.same
      case CheckToken(token, replyTo) =>
        if (tokens.contains(token)) {
          JwtCirce.decode(token, key, Seq(JwtAlgorithm.HS256)) match {
            case Success(decoded) =>
              if (decoded.expiration.getOrElse(0L) > Instant.now.getEpochSecond)
                replyTo ! TokenValid(token)
            case Failure(_) => replyTo ! TokenInvalid(token)
          }
        } else replyTo ! TokenInvalid(token)
        Behaviors.same
      case Logout(token, replyTo) =>
        if (tokens.contains(token)) {
          tokens -= token
          replyTo ! LogoutSuccessful(token)
        } else
          replyTo ! LogoutUnsuccessful(token)
        Behaviors.same
    }
}
