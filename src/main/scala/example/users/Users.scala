package example.users

import cats.effect.kernel.MonadCancelThrow
import cats.effect.kernel.Ref
import cats.effect.kernel.Sync
import cats.syntax.functor._
import doobie.util.transactor.Transactor
import example.users.Users.NewUser
import example.users.Users.User
import example.users.Users.UserId

trait Users[F[_]] {
  def register(user: NewUser): F[UserId]

  def getUsers: F[List[User]]
}

object Users {
  def live[F[_]: MonadCancelThrow](xa: Transactor[F]): Users[F] =
    new PostgresUsers[F](xa)

  def test[F[_]: Sync]: F[Users[F]] =
    Ref.empty[F, List[User]].map(new InMemoryUsers[F](_))

  case class UserId(value: String) extends AnyVal
  case class FirstName(value: String) extends AnyVal
  case class LastName(value: String) extends AnyVal
  case class NewUser(firstName: FirstName, lastName: LastName)
  case class User(id: UserId, firstName: FirstName, lastName: LastName)
}
