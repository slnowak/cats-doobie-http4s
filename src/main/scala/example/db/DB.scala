package example.db

import cats.effect.Async
import cats.effect.kernel.Resource
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

object DB {

  def instance[F[_]: Async]: Resource[F, Transactor[F]] =
    for {
      postgresConfig <- Postgres.instance
      transactor <- makeTransactor(postgresConfig)
      _ <- Migrations.apply(transactor)
    } yield transactor

  private def makeTransactor[F[_]: Async](
    config: PostgresConnectionConfig
  ): Resource[F, HikariTransactor[F]] =
    for {
      ec <- ExecutionContexts.fixedThreadPool(3)
      xa <- HikariTransactor.newHikariTransactor(
        driverClassName = "org.postgresql.Driver",
        url = config.jdbcUrl,
        user = config.username,
        pass = config.password,
        connectEC = ec
      )
    } yield xa
}
