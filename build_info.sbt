import dev.quadstingray.sbt.json.JsonFile

val json = JsonFile(file("package.json"))

val QuadStingrayHomepage = "https://www.quadstingray.dev"

ThisBuild / organizationHomepage := Some(url(QuadStingrayHomepage))

ThisBuild / homepage := Some(url(json.stringValue("homepage")))

ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/QuadStingray/quartz-manager"), "scm:https://github.com/QuadStingray/quartz-manager.git"))

ThisBuild / developers := List(Developer(id = "quadstingray", name = "QuadStingray", email = "info@quadstingray.dev", url = url(QuadStingrayHomepage)))

ThisBuild / licenses += (json.stringValue("license"), url("https://github.com/quadstingray/quartz-manager/blob/main/LICENSE"))
