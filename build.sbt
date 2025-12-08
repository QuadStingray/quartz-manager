import common.quartzManagerProject
import dev.quadstingray.sbt.json.JsonFile

lazy val root = Project(id = "quartz-manager-parent", base = file(".")).aggregate(api, ui)

val scalaVersions = Seq("2.13.18", "3.7.4")

crossScalaVersions := scalaVersions

ThisBuild / crossScalaVersions := scalaVersions

ThisBuild / scalaVersion := scalaVersions.head

val json = JsonFile(file("package.json"))

ThisBuild / organization := json.stringValue("organization")

publish / skip := true

lazy val api = quartzManagerProject("api").enablePlugins(BuildInfoPlugin)

lazy val ui = quartzManagerProject("ui").dependsOn(api).enablePlugins(BuildInfoPlugin)
