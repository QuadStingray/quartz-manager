package dev.quadstingray.quartz.manager.api.routes
import dev.quadstingray.quartz.manager.api.json.CirceSchema
import dev.quadstingray.quartz.manager.api.model.Error
import dev.quadstingray.quartz.manager.api.model.ErrorResponse
import dev.quadstingray.quartz.manager.api.model.LogRecord
import dev.quadstingray.quartz.manager.api.service.auth.AuthenticationService
import dev.quadstingray.quartz.manager.api.service.HistoryService
import dev.quadstingray.quartz.manager.api.ActorHandler
import io.circe.generic.auto._
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

class HistoryRoutes(authenticationService: AuthenticationService) extends CirceSchema {
  implicit val ex: ExecutionContext = ActorHandler.requestExecutionContext

  private val historyApiBaseEndpoint = authenticationService.securedEndpointDefinition.tag("History").in("api" / "history")

  private val historyListEndpoint = historyApiBaseEndpoint
    .out(jsonBody[List[LogRecord]])
    .summary("Jobs History")
    .description("Returns the List of all Jobs History with full information")
    .method(Method.GET)
    .name("historyList")
    .serverLogic {
      _ => _ =>
        Future {
          Right {
            HistoryService.cache.asMap().values.toList
          }
        }
    }

  private val historyByIdEndpoint = historyApiBaseEndpoint
    .in(path[String]("id"))
    .out(jsonBody[LogRecord])
    .summary("Job History by ID")
    .description("Returns a single Job History record by its ID")
    .method(Method.GET)
    .name("historyById")
    .serverLogic {
      _ => id =>
        Future {
          HistoryService.cache.asMap().get(id) match {
            case Some(logRecord) => Right(logRecord)
            case None            => Left(Error(StatusCode.NotFound, ErrorResponse(s"History with ID $id not found")))
          }
        }
    }

  lazy val endpoints: List[ServerEndpoint[PekkoStreams with capabilities.WebSockets, Future]] = {
    List(historyListEndpoint, historyByIdEndpoint)
  }
}
