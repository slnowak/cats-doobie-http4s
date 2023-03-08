package example

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import example.users.UserRoutes
import example.users.Users

object App extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    val database = DB.instance[IO]
    database.use { xa =>
      val users = Users.live[IO](xa)
      val userRoutes = UserRoutes[IO](users)
      HttpServer.start[IO](userRoutes).as(ExitCode.Success)
    }
  }
}
