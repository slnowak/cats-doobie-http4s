package example.users

import cats.effect.kernel.Ref
import cats.effect.kernel.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import example.users.Users.NewUser
import example.users.Users.User
import example.users.Users.UserId

import java.util.UUID

private class InMemoryUsers[F[_]: Sync](val users: Ref[F, List[User]])
    extends Users[F] {
  override def register(user: NewUser): F[UserId] =
    for {
      userId <- generateUserId
      _ <- users.update(
        allUsers => allUsers :+ User(userId, user.firstName, user.lastName)
      )
    } yield userId

  private def generateUserId: F[UserId] =
    Sync[F].delay(UUID.randomUUID()).map(userId => UserId(userId.toString))

  override def getUsers: F[List[User]] =
    users.get
}
