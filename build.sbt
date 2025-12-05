import common.quarzManagerProject
import dev.quadstingray.sbt.json.JsonFile

lazy val root = Project(id = "quartz-manager-parent", base = file(".")).aggregate(api, ui)

ThisBuild / scalaVersion := "2.13.18"

val json = JsonFile(file("package.json"))

ThisBuild / organization := json.stringValue("organization")

publish / skip := true

lazy val api = quarzManagerProject("api").enablePlugins(BuildInfoPlugin)

lazy val ui = quarzManagerProject("ui").dependsOn(api).enablePlugins(BuildInfoPlugin)
