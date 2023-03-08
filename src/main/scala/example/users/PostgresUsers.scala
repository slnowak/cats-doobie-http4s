package example.users

import cats.effect.kernel.MonadCancelThrow
import doobie.implicits._
import doobie.util.transactor.Transactor
import example.users.Users.User
import example.users.Users.UserId

private class PostgresUsers[F[_]: MonadCancelThrow](xa: Transactor[F])
    extends Users[F] {
  override def register(user: Users.NewUser): F[UserId] = {
    val insertStatement =
      sql"""INSERT INTO users (firstname, lastname) VALUES (${user.firstName}, ${user.lastName})"""

    insertStatement.update
      .withUniqueGeneratedKeys[UserId]("user_id")
      .transact(xa)
  }

  override def getUsers: F[List[User]] = {
    val allUsersQuery = sql"""SELECT user_id, firstname, lastname FROM users"""
    allUsersQuery.query[User].to[List].transact(xa)
  }
}
