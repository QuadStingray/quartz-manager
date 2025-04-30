package dev.quadstingray.quartz.manager.api.routes
import dev.quadstingray.quartz.manager.api.json.CirceSchema
import dev.quadstingray.quartz.manager.api.model.JobConfig
import dev.quadstingray.quartz.manager.api.model.JobInformation
import dev.quadstingray.quartz.manager.api.model.ModelConstants
import dev.quadstingray.quartz.manager.api.ActorHandler
import io.circe.generic.auto._
import org.quartz.Job
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import sttp.capabilities
import sttp.capabilities.pekko.PekkoStreams
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.model.Method
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.endpoint
import sttp.tapir.generic.auto._
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.model.UsernamePassword
import sttp.tapir.server.ServerEndpoint

object JobRoutes extends CirceSchema {
  implicit val ex: ExecutionContext = ActorHandler.requestExecutionContext
  private val jobApiBaseEndpoint    = endpoint.tag("Jobs").in("system" / "jobs")

  val jobsListRoutes = jobApiBaseEndpoint
    .out(jsonBody[List[JobInformation]])
    .summary("Registered Jobs")
    .description("Returns the List of all registered Jobs with full information")
    .method(Method.GET)
    .name("jobsList")
    .serverLogic(
      _ => jobsList()
    )

  def jobsList(): Future[Either[Unit, List[JobInformation]]] = {
    Future { Right { List.empty } }
  }

  val registerJobRoutes = jobApiBaseEndpoint
    .in(jsonBody[JobConfig])
    .out(jsonBody[Option[JobInformation]])
    .summary("Register Job")
    .description("Register an Job and return the JobInformation with next schedule information")
    .method(Method.PUT)
    .name("registerJob")
    .serverLogic(
      config => registerJob(config)
    )

  def registerJob(jobConfig: JobConfig): Future[Either[Unit, Option[JobInformation]]] = {
    Future { Right { None } }
  }

  lazy val jobGroupParameter =
    path[String]("jobGroup")
      .description("Group Name of the Job")
      .default(ModelConstants.jobDefaultGroup)
      .and(path[String]("jobName").description("Name of the Job"))

  // https://www.freeformatter.com/cron-expression-generator-quartz.html
  val updateJobRoutes = jobApiBaseEndpoint
    .in(jobGroupParameter)
    .in(jsonBody[JobConfig])
    .out(jsonBody[Option[JobInformation]])
    .summary("Update Job")
    .description("Add Job and get JobInformation back")
    .method(Method.PATCH)
    .name("updateJob")
    .serverLogic(
      parameter => updateJob(parameter)
    )

  def updateJob(parameter: (String, String, JobConfig)): Future[Either[Unit, Option[JobInformation]]] = {
    Future { Right { None } }
  }

  val deleteJobRoutes = jobApiBaseEndpoint
    .in(jobGroupParameter)
    .out(statusCode(StatusCode.NoContent).description("Job deleted"))
    .summary("Delete Job")
    .description("Delete Job and reload all Job Information")
    .method(Method.DELETE)
    .name("deleteJob")
    .serverLogic(
      parameter => deleteJob(parameter)
    )

  def deleteJob(parameter: (String, String)): Future[Either[Unit, Unit]] = {
    Future { Right { List.empty } }
  }

  val jobClassesRoutes = jobApiBaseEndpoint
    .in("classes")
    .out(jsonBody[List[String]])
    .summary("Possible Jobs")
    .description("Returns the List of possible job classes")
    .method(Method.GET)
    .name("possibleJobsList")
    .serverLogic(
      _ => jobClassesList()
    )

  def jobClassesList(): Future[Either[Unit, List[String]]] = {
//      ReflectionService.getSubClassesList(classOf[Job]).map(_.getName).filterNot(_.startsWith("org.quartz")).sorted
    Future { Right { List.empty } }
  }

  val executeJobRoutes = jobApiBaseEndpoint
    .in(jobGroupParameter)
    .in(jsonBody[Map[String, Any]].description("Job Data map"))
    .out(statusCode(StatusCode.NoContent).description("Job added to trigger"))
    .summary("Execute Job")
    .description("Execute scheduled Job manually")
    .method(Method.POST)
    .name("executeJob")
    .serverLogic(
      parameter => executeJob(parameter)
    )

  def executeJob(parameter: (String, String, Map[String, Any])): Future[Either[Unit, Unit]] = {
    ???
  }

  lazy val endpoints: List[ServerEndpoint[PekkoStreams with capabilities.WebSockets, Future]] = {
    List(jobsListRoutes, registerJobRoutes, updateJobRoutes, deleteJobRoutes, executeJobRoutes, jobClassesRoutes)
  }
}
