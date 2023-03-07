package example

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val routes = GreeterRoutes.apply[IO]
    HttpServer.start[IO](routes).as(ExitCode.Success)
  }
}
