package example.db

import cats.effect.Resource
import cats.effect.kernel.Sync
import org.testcontainers.containers.PostgreSQLContainer

private object Postgres {
  type Postgres = PostgreSQLContainer[Nothing]

  def instance[F[_]: Sync]: Resource[F, PostgresConnectionConfig] =
    postgresContainer.map(
      container =>
        PostgresConnectionConfig(
          container.getJdbcUrl,
          container.getUsername,
          container.getPassword
      )
    )

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

}

private case class PostgresConnectionConfig(jdbcUrl: String,
                                            username: String,
                                            password: String)
