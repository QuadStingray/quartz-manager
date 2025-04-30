import sbt.{Project, file}
object common {
  def quarzManagerProject(name: String) = {
    val project = Project(s"quarz-manager-${name}", file(s"quarz-manager-${name}"))
    project
  }
}