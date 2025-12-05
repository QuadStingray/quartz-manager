import common.quarzManagerProject
import dev.quadstingray.sbt.json.JsonFile

lazy val root = Project(id = "quartz-manager-parent", base = file(".")).aggregate(api, ui)

val scalaVersions = Seq("2.13.18", "3.7.4")

crossScalaVersions := scalaVersions

ThisBuild / scalaVersion := scalaVersions.head

val json = JsonFile(file("package.json"))

ThisBuild / organization := json.stringValue("organization")

publish / skip := true

lazy val api = quarzManagerProject("api").enablePlugins(BuildInfoPlugin)

lazy val ui = quarzManagerProject("ui").dependsOn(api).enablePlugins(BuildInfoPlugin)
