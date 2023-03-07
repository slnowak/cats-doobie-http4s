package example

import cats.effect.Async
import org.http4s.blaze.server.BlazeServerBuilder

object HttpServer {

  def start[F[_]: Async](routes: GreeterRoutes[F]): F[Nothing] =
    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(routes.definition.orNotFound)
      .resource
      .useForever
}
