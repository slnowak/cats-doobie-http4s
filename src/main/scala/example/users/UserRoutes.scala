package example.users

import cats.effect.kernel.Concurrent
import cats.syntax.flatMap._
import cats.syntax.functor._
import example.users.Users.NewUser
import example.users.Users.UserId
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

case class UserRoutes[F[_]] private (definition: HttpRoutes[F])
object UserRoutes {

  def apply[F[_]: Concurrent](users: Users[F]): UserRoutes[F] = {
    val dsl = Http4sDsl[F]

    import Json._
    import dsl._

    val routes = HttpRoutes.of[F] {
      case GET -> Root / "users" =>
        Ok(users.getUsers)

      case request @ POST -> Root / "users" =>
        for {
          newUser <- request.decodeJson[NewUser]
          userId <- users.register(newUser)
          response <- Created(UserCreated(userId))
        } yield response

      case GET -> Root / "users" / UUIDVar(id) =>
        val userId = UserId(id)
        users
          .getUser(userId)
          .foldF(NotFound(s"User [userId = $id] not found"))(user => Ok(user))
    }

    UserRoutes(routes)
  }
}
