package dev.quadstingray.quartz.manager.api.routes

import dev.quadstingray.quartz.manager.api.json.CirceSchema
import dev.quadstingray.quartz.manager.api.model.Overview
import dev.quadstingray.quartz.manager.api.model.SchedulerInformation
import dev.quadstingray.quartz.manager.api.model.SystemOverview
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

class SystemRoutes(authenticationService: AuthenticationService, scheduler: Scheduler) extends CirceSchema {

  implicit val ex: ExecutionContext = ActorHandler.requestExecutionContext

  private val systemApiBaseEndpoint = authenticationService.securedEndpointDefinition.tag("System").in("api" / "system")

  private val getSystemInformation = systemApiBaseEndpoint
    .out(jsonBody[Overview])
    .summary("Get System Information")
    .description("Get all Information about the system and current quartz scheduler")
    .method(Method.GET)
    .name("schedulerOverview")
    .serverLogicSuccess {
      _ => _ =>
        Future {
          Overview(scheduler)
        }
    }

  lazy val endpoints: List[ServerEndpoint[PekkoStreams with capabilities.WebSockets, Future]] = {
    List(getSystemInformation)
  }
}
