name := """kamon-showcase"""
organization := "kamon.showcase"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += filters
libraryDependencies ++= Seq(
  "io.kamon" %% "kamon-play-2.5" % "0.6.6",
  "io.kamon" %% "kamon-akka-2.4" % "0.6.6",
  "io.kamon" %% "kamon-system-metrics" % "0.6.6",
  "io.kamon" %% "kamon-graphite" % "1.1.0"

)

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "kamon.showcase.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "kamon.showcase.binders._"
