package dev.quadstingray.quartz.manager.api.routes
import dev.quadstingray.quartz.manager.api.json.CirceSchema
import dev.quadstingray.quartz.manager.api.model.Error
import dev.quadstingray.quartz.manager.api.model.ErrorResponse
import dev.quadstingray.quartz.manager.api.model.LogRecord
import dev.quadstingray.quartz.manager.api.service.auth.AuthenticationService
import dev.quadstingray.quartz.manager.api.service.HistoryService
import dev.quadstingray.quartz.manager.api.util.LuceneQueryParser
import dev.quadstingray.quartz.manager.api.util.SortUtility
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
    .in(query[Option[String]]("query").description("Lucene query string for filtering (e.g., 'jobGroup:batch AND className:MyJob')"))
    .in(query[Option[String]]("sort").description("Comma-separated sort fields, prefix with '-' for descending (e.g., '-date,className')"))
    .out(jsonBody[List[LogRecord]])
    .summary("Jobs History")
    .description("Returns the List of all Jobs History with full information")
    .method(Method.GET)
    .name("historyList")
    .serverLogic {
      _ => (params: (Option[String], Option[String])) =>
        Future {
          Right {
            val (queryOpt, sortStringOpt) = params
            val sortOpt                   = sortStringOpt.map(_.split(",").toList.map(_.trim).filter(_.nonEmpty))
            val allRecords                = HistoryService.cache.asMap().values.toList

            // Define field extractors for filtering
            val filterExtractors: Map[String, LogRecord => Option[String]] = Map(
              "id" -> (r => Some(r.id)),
              "className" -> (r => Some(r.className)),
              "jobGroup" -> (r => r.jobGroup),
              "jobName" -> (r => r.jobName)
            )

            // Define field extractors for sorting
            val sortExtractors: Map[String, LogRecord => Option[Comparable[Any]]] = Map(
              "id" -> (r => Some(r.id.asInstanceOf[Comparable[Any]])),
              "className" -> (r => Some(r.className.asInstanceOf[Comparable[Any]])),
              "date" -> (r => Some(r.date.asInstanceOf[Comparable[Any]])),
              "jobGroup" -> (r => r.jobGroup.map(_.asInstanceOf[Comparable[Any]])),
              "jobName" -> (r => r.jobName.map(_.asInstanceOf[Comparable[Any]]))
            )

            // Apply filtering
            val filtered = allRecords.filter(LuceneQueryParser.parseAndFilter(queryOpt, filterExtractors))

            // Apply sorting
            SortUtility.sort(filtered, sortOpt, sortExtractors)
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
