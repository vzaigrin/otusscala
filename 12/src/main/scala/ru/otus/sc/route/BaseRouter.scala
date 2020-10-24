package ru.otus.sc.route

import akka.http.scaladsl.server.Route
import sttp.tapir.Endpoint

trait BaseRouter {
  def endpoints: List[Endpoint[_, _, _, _]]
  def route: Route
}
