package example.db
import cats.effect.Sync
import cats.effect.kernel.Resource
import cats.syntax.flatMap._
import cats.syntax.functor._
import de.lhns.doobie.flyway.BaselineMigrations.BaselineMigrationOps
import de.lhns.doobie.flyway.Flyway
import doobie.hikari.HikariTransactor
private object Migrations {

  def apply[F[_]: Sync](xa: HikariTransactor[F]): Resource[F, Unit] =
    Flyway(xa) { flyway =>
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
