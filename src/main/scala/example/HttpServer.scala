package example

import cats.effect.Async
import example.users.UserRoutes
import org.http4s.blaze.server.BlazeServerBuilder

object HttpServer {

  def start[F[_]: Async](routes: UserRoutes[F]): F[Nothing] =
    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(routes.definition.orNotFound)
      .resource
      .useForever
}
