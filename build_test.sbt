ThisBuild / Test / parallelExecution := false
ThisBuild / parallelExecution        := false
Global / parallelExecution           := false
Global / concurrentRestrictions ++= Seq(Tags.limit(Tags.Test, 1), Tags.limit(Tags.Test, 1))

ThisBuild / Test / scalacOptions ++= Seq("-Yrangepos")

libraryDependencies += "org.scalameta" %% "munit" % "1.1.1" % Test

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.18" % Test
