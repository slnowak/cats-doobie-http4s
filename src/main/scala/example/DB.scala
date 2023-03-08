package example

import cats.effect.Async
import cats.effect.kernel.Resource
import cats.effect.kernel.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import de.lhns.doobie.flyway.BaselineMigrations.BaselineMigrationOps
import de.lhns.doobie.flyway.Flyway
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.testcontainers.containers.PostgreSQLContainer

object DB {
  type Postgres = PostgreSQLContainer[Nothing]

  def instance[F[_]: Async]: Resource[F, Transactor[F]] =
    for {
      postgres <- postgresContainer
      config = DbConfig(
        postgres.getJdbcUrl,
        postgres.getUsername,
        postgres.getPassword
      )
      transactor <- makeTransactor(config)
      _ <- applyMigrations(transactor)
    } yield transactor

  private def postgresContainer[F[_]: Sync]: Resource[F, Postgres] = {
    val startedContainer = Sync[F].blocking {
      val container = new PostgreSQLContainer("postgres:15.2")
      container.start()
      container
    }
    val containerCleanup: Postgres => F[Unit] = container =>
      Sync[F].blocking(container.stop())

    Resource.make(startedContainer)(containerCleanup)
  }

  private def makeTransactor[F[_]: Async](
    config: DbConfig
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

  private def applyMigrations[F[_]: Sync](
    xa: HikariTransactor[F]
  ): Resource[F, Unit] = Flyway(xa) { flyway =>
    for {
      info <- flyway.info()
      _ <- flyway
        .configure(
          _.withBaselineMigrate(info)
            .validateMigrationNaming(true)
        )
        .migrate()
    } yield ()
  }
}

private case class DbConfig(jdbcUrl: String, username: String, password: String)
