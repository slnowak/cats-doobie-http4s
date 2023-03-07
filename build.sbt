ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(name := "catseffect-tagless")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % "0.23.18",
  "org.http4s" %% "http4s-blaze-server" % "0.23.13",
  "org.http4s" %% "http4s-circe" % "0.23.18",
  "io.circe" %% "circe-generic" % "0.14.5",
  "io.circe" %% "circe-generic-extras" % "0.14.3",
  "org.typelevel" %% "cats-effect" % "3.4.8"
)
