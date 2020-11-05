package ru.otus.sc.route

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import sttp.tapir.Endpoint
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.docs.openapi._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

class Router(
    authorRouter: BaseRouter,
    bookRoute: BaseRouter,
    recordRouter: BaseRouter,
    roleRouter: BaseRouter,
    userRouter: BaseRouter
) extends BaseRouter {

  def endpoints: List[Endpoint[_, _, _, _]] =
    authorRouter.endpoints ++ bookRoute.endpoints ++ recordRouter.endpoints ++ roleRouter.endpoints ++ userRouter.endpoints

  private val openApiDocs: OpenAPI = endpoints.toOpenAPI("Books Library", "1.0.0")
  private val openApiYml: String   = openApiDocs.toYaml

  def route: Route =
    concat(
      authorRouter.route,
      bookRoute.route,
      recordRouter.route,
      roleRouter.route,
      userRouter.route,
      new SwaggerAkka(openApiYml).routes
    )
}
