package ru.otus.sc.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class Router(
    authorRouter: BaseRouter,
    bookRoute: BaseRouter,
    roleRouter: BaseRouter,
    userRouter: BaseRouter,
    recordRouter: BaseRouter
) extends BaseRouter {
  def route: Route =
    pathPrefix("api" / "v1") {
      concat(
        authorRouter.route,
        bookRoute.route,
        roleRouter.route,
        userRouter.route,
        recordRouter.route
      )
    }
}
