package example

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Ref
import example.users.UserRoutes
import example.users.Users

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    for {
      users <- Users.instance[IO]
      userRoutes = UserRoutes[IO](users)
      _ <- HttpServer.start[IO](userRoutes)
    } yield ExitCode.Success
}
