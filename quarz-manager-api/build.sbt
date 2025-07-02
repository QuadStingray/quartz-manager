name := "quartz-manager-api"

buildInfoPackage := "dev.quadstingray.quartz.manager.api"

val TapirVersion = "1.11.32"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-pekko-http-server" % TapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"      % TapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % TapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % TapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"       % TapirVersion

libraryDependencies += "com.typesafe" % "config" % "1.4.3"

libraryDependencies += "com.github.jwt-scala" %% "jwt-circe" % "10.0.4"

libraryDependencies += "org.quartz-scheduler" % "quartz" % "2.5.0"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

libraryDependencies += "joda-time" % "joda-time" % "2.14.0"

libraryDependencies += "io.github.classgraph" % "classgraph" % "4.8.179"

buildInfoOptions += BuildInfoOption.BuildTime
