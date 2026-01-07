name := "quartz-manager-api"

buildInfoPackage := "dev.quadstingray.quartz.manager.api"

val TapirVersion = "1.13.4"
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-pekko-http-server" % TapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"      % TapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-circe"        % TapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % TapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-sttp-client"       % TapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-files"             % TapirVersion

libraryDependencies += "com.github.jwt-scala" %% "jwt-circe" % "11.0.3"

libraryDependencies += "com.typesafe" % "config" % "1.4.5"

libraryDependencies += "org.quartz-scheduler" % "quartz" % "2.5.2"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.6"

libraryDependencies += "joda-time" % "joda-time" % "2.14.0"

libraryDependencies += "io.github.classgraph" % "classgraph" % "4.8.184"

libraryDependencies += "com.github.blemale" %% "scaffeine" % "5.3.0"

libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "10.3.2"

buildInfoOptions += BuildInfoOption.BuildTime
