package example.users

import cats.data.OptionT
import cats.effect.kernel.MonadCancelThrow
import doobie.Meta
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor
import example.users.Users.User
import example.users.Users.UserId

import java.util.UUID

private class PostgresUsers[F[_]: MonadCancelThrow](xa: Transactor[F])
    extends Users[F] {

  override def register(user: Users.NewUser): F[UserId] = {
    val insertStatement =
      sql"""INSERT INTO users (firstname, lastname) VALUES (${user.firstName}, ${user.lastName})"""

    insertStatement.update
      .withUniqueGeneratedKeys[UserId]("user_id")
      .transact(xa)
  }

  override def getUser(id: UserId): OptionT[F, User] = {
    val findUserQuery =
      sql"""SELECT user_id, firstname, lastname FROM users WHERE user_id = $id LIMIT 1"""

    OptionT(
      findUserQuery
        .query[User]
        .option
        .transact(xa)
    )
  }

  override def getUsers: F[List[User]] = {
    val allUsersQuery = sql"""SELECT user_id, firstname, lastname FROM users"""
    allUsersQuery.query[User].to[List].transact(xa)
  }
}

private object PostgresUsers {
  implicit val userIdMeta: Meta[UserId] =
    Meta[UUID].imap[UserId](UserId)(_.value)
}
