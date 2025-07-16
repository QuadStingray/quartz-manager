package dev.quadstingray.quartz.manager.api.routes

import dev.quadstingray.quartz.manager.api.json.CirceSchema
import dev.quadstingray.quartz.manager.api.model.JobConfig
import dev.quadstingray.quartz.manager.api.model.JobInformation
import dev.quadstingray.quartz.manager.api.model.ModelConstants
import dev.quadstingray.quartz.manager.api.service.auth.AuthenticationService
import dev.quadstingray.quartz.manager.api.service.ClassGraphService
import dev.quadstingray.quartz.manager.api.service.JobSchedulerService
import dev.quadstingray.quartz.manager.api.ActorHandler
import io.circe.generic.auto._
import org.quartz.Job
import org.quartz.Scheduler
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import sttp.capabilities
import sttp.capabilities.pekko.PekkoStreams
import sttp.model.Method
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint

class JobRoutes(authenticationService: AuthenticationService, classGraphService: ClassGraphService, scheduler: Scheduler) extends CirceSchema {
  implicit val ex: ExecutionContext   = ActorHandler.requestExecutionContext
  val jobService: JobSchedulerService = new JobSchedulerService(classGraphService, scheduler)

  private val jobApiBaseEndpoint = authenticationService.securedEndpointDefinition.tag("Jobs").in("api" / "jobs")

  val jobsListEndpoint = jobApiBaseEndpoint
    .out(jsonBody[List[JobInformation]])
    .summary("Registered Jobs")
    .description("Returns the List of all registered Jobs with full information")
    .method(Method.GET)
    .name("jobsList")
    .serverLogic {
      _ => _ =>
        Future {
          Right {
            jobService.jobsList()
          }
        }
    }

  val registerJobEndpoint = jobApiBaseEndpoint
    .in(jsonBody[JobConfig])
    .out(jsonBody[JobInformation])
    .summary("Register Job")
    .description("Register an Job and return the JobInformation with next schedule information")
    .method(Method.PUT)
    .name("registerJob")
    .serverLogic(
      _ =>
        config =>
          Future {
            Right {
              jobService.scheduleJob(config)
            }
          }
    )

  lazy val jobGroupParameter =
    path[String]("jobGroup")
      .description("Group Name of the Job")
      .default(ModelConstants.jobDefaultGroup)
      .and(path[String]("jobName").description("Name of the Job"))

  // https://www.freeformatter.com/cron-expression-generator-quartz.html
  val updateJobEndpoint = jobApiBaseEndpoint
    .in(jobGroupParameter)
    .in(jsonBody[JobConfig])
    .out(jsonBody[JobInformation])
    .summary("Update Job")
    .description("Add Job and get JobInformation back")
    .method(Method.PATCH)
    .name("updateJob")
    .serverLogic(
      _ =>
        parameter =>
          Future {
            Right {
              jobService.removeJobFromScheduler(parameter._1, parameter._2)
              jobService.scheduleJob(parameter._3)
            }
          }
    )

  val deleteJobEndpoint = jobApiBaseEndpoint
    .in(jobGroupParameter)
    .out(statusCode(StatusCode.NoContent).description("Job deleted"))
    .summary("Delete Job")
    .description("Delete Job and reload all Job Information")
    .method(Method.DELETE)
    .name("deleteJob")
    .serverLogic(
      _ =>
        parameter =>
          Future {
            Right {
              jobService.removeJobFromScheduler(parameter._1, parameter._2)
            }
          }
    )

  val jobClassesEndpoint = jobApiBaseEndpoint
    .in("classes")
    .out(jsonBody[List[String]])
    .summary("Possible Jobs")
    .description("Returns the List of possible job classes")
    .method(Method.GET)
    .name("possibleJobsList")
    .serverLogic(
      _ =>
        _ =>
          Future {
            Right {
              classGraphService
                .getSubClassesList(classOf[Job])
                .filter(
                  cI => !cI.isAbstract && !cI.isInterface
                )
                .map(_.getName)
            }
          }
    )

  val executeJobEndpoint = jobApiBaseEndpoint
    .in(jobGroupParameter)
    .in(jsonBody[Option[Map[String, Any]]].description("Job Data map"))
    .out(statusCode(StatusCode.NoContent).description("Job added to trigger"))
    .summary("Execute Job")
    .description("Execute scheduled Job manually")
    .method(Method.POST)
    .name("executeJob")
    .serverLogic(
      _ =>
        parameter =>
          Future {
            Right {
              jobService.executeJob(parameter._1, parameter._2, parameter._3)
            }
          }
    )

  lazy val endpoints: List[ServerEndpoint[PekkoStreams with capabilities.WebSockets, Future]] = {
    List(jobsListEndpoint, registerJobEndpoint, updateJobEndpoint, deleteJobEndpoint, executeJobEndpoint, jobClassesEndpoint)
  }

}
