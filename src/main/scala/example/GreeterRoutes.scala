package example

import cats.Monad
import org.http4s.HttpRoutes
import org.http4s.dsl._

case class GreeterRoutes[F[_]] private (definition: HttpRoutes[F])
object GreeterRoutes {

  def apply[F[_]: Monad]: GreeterRoutes[F] = {
    val dsl = Http4sDsl[F]

    import dsl._

    val routes = HttpRoutes.of[F] {
      case GET -> Root / "greeter" / "hello" =>
        Ok("Hello world")
    }

    GreeterRoutes(routes)
  }
}
