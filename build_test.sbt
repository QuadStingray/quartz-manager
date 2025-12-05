import scala.collection.immutable.Seq
ThisBuild / Test / parallelExecution := false
ThisBuild / parallelExecution        := false
Global / parallelExecution           := false
Global / concurrentRestrictions ++= Seq(Tags.limit(Tags.Test, 1), Tags.limit(Tags.Test, 1))

ThisBuild / Test / scalacOptions ++= Seq("-Yrangepos")

libraryDependencies += "org.scalameta" %% "munit" % "1.2.1" % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.21" % Test

val sttpVersion = "3.11.0"
libraryDependencies += "com.softwaremill.sttp.client3" %% "core"          % sttpVersion % Test
libraryDependencies += "com.softwaremill.sttp.client3" %% "circe"         % sttpVersion % Test
libraryDependencies += "joda-time"                      % "joda-time"     % "2.14.0"    % Test
libraryDependencies += "io.circe"                      %% "circe-generic" % "0.14.15"   % Test