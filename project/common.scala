import sbt.{Project, file}
object common {
  def quartzManagerProject(name: String) = {
    val project = Project(s"quartz-manager-${name}", file(s"quartz-manager-${name}"))
    project
  }
}