package dev.quadstingray.quartz.manager.api.routes

import dev.quadstingray.quartz.manager.api.json.CirceSchema
import dev.quadstingray.quartz.manager.api.model.SchedulerInformation
import dev.quadstingray.quartz.manager.api.model.Status
import dev.quadstingray.quartz.manager.api.service.auth.AuthenticationService
import dev.quadstingray.quartz.manager.api.ActorHandler
import io.circe.generic.auto._
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

class SchedulerRoutes(authenticationService: AuthenticationService, scheduler: Scheduler) extends CirceSchema {
  implicit val ex: ExecutionContext = ActorHandler.requestExecutionContext

  private val schedulerApiBaseEndpoint = authenticationService.securedEndpointDefinition.tag("Scheduler").in("api" / "scheduler")

  private val getSchedulerInformation = schedulerApiBaseEndpoint
    .out(jsonBody[SchedulerInformation])
    .summary("Get Scheduler Information")
    .description("Get all Informations about the current Quartz Scheduler")
    .method(Method.GET)
    .name("schedulerInformation")
    .serverLogicSuccess {
      _ => _ =>
        Future {
          SchedulerInformation(scheduler)
        }
    }

  private val startSchedulerRoutes = schedulerApiBaseEndpoint
    .in("start")
    .out(statusCode(StatusCode.NoContent).description("Scheduler started"))
    .summary("Start Scheduler")
    .description("Start the Quartz Scheduler")
    .method(Method.POST)
    .name("startScheduler")
    .serverLogic {
      _ => _ =>
        Future {
          Right {
            scheduler.start()
          }
        }
    }

  private val standbySchedulerRoutes = schedulerApiBaseEndpoint
    .in("standby")
    .out(statusCode(StatusCode.NoContent).description("Scheduler standby"))
    .summary("Standby Scheduler")
    .description("Standby the Quartz Scheduler")
    .method(Method.POST)
    .name("standbyScheduler")
    .serverLogic {
      _ => _ =>
        Future {
          Right {
            scheduler.standby()
          }
        }
    }

  private val shutdownSchedulerRoutes = schedulerApiBaseEndpoint
    .in("shutdown")
    .in(query[Boolean]("waitForJobsToComplete").default(true))
    .out(statusCode(StatusCode.NoContent).description("Scheduler shutdown"))
    .summary("Shutdown Scheduler")
    .description("Shutdown the Quartz Scheduler")
    .method(Method.POST)
    .name("shutdownScheduler")
    .serverLogic {
      _ => parameter =>
        Future {
          Right {
            scheduler.shutdown(parameter)
          }
        }
    }

  lazy val endpoints: List[ServerEndpoint[PekkoStreams with capabilities.WebSockets, Future]] = {
    List(getSchedulerInformation, startSchedulerRoutes, standbySchedulerRoutes, shutdownSchedulerRoutes)
  }
}
