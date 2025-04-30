package dev.quadstingray.quartz.manager.api.routes.docs

import dev.quadstingray.quartz.manager.api.json.CirceSchema
import dev.quadstingray.quartz.manager.api.model.JobConfig
import dev.quadstingray.quartz.manager.api.model.JobInformation
import dev.quadstingray.quartz.manager.api.model.ModelConstants
import dev.quadstingray.quartz.manager.api.ActorHandler
import dev.quadstingray.quartz.manager.api.BuildInfo
import io.circe.generic.auto._
import org.quartz.Job
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.capabilities
import sttp.capabilities.pekko.PekkoStreams
import sttp.capabilities.WebSockets
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.model.Method
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.endpoint
import sttp.tapir.generic.auto._
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.model.UsernamePassword
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.swagger.SwaggerUIOptions
object ApiDocsRoutes extends CirceSchema with SchemaDerivation {
  val nameOpenApiDocsYamlName = "docs.yaml"

  def docsYamlEndpoint(yamlName: String, content: String): ServerEndpoint[PekkoStreams with WebSockets, Future] = {
    def contentToResponse(): Future[Either[Unit, (String, Long)]] = {
      Future.successful(Right {
        (content, content.getBytes.length)
      })
    }

    val myEndpoint = endpoint
      .in("docs" / yamlName)
      .out(stringBody)
      .out(header("Content-Type", "text/yaml"))
      .out(header("Content-Disposition", "inline; filename=\"%s\"".format(yamlName)))
      .out(header[Long]("Content-Length"))
      .tag("Docs")
      .method(Method.GET)
      .name(yamlName)
      .serverLogic(
        _ => contentToResponse()
      )

    myEndpoint
  }

  def addDocsRoutes(serverEndpoints: List[ServerEndpoint[PekkoStreams with WebSockets, Future]]): List[ServerEndpoint[PekkoStreams with WebSockets, Future]] = {
    val docs = ArrayBuffer[ServerEndpoint[PekkoStreams with WebSockets, Future]]()

    val swaggerEnabled = isSwaggerEnabled
    if (swaggerEnabled) {
      val openApiDocs = OpenAPIDocsInterpreter().toOpenAPI(serverEndpoints.map(_.endpoint), BuildInfo.name, BuildInfo.version)

      val openApiYml: String = openApiDocs.toYaml

      if (swaggerEnabled) {
        val swaggerUIRoute =
          SwaggerUI[Future](
            openApiYml,
            SwaggerUIOptions(List("docs"), nameOpenApiDocsYamlName, List(), useRelativePaths = true, showExtensions = false, None, None)
          )
        docs ++= swaggerUIRoute
      }

      if (!swaggerEnabled) {
        docs += docsYamlEndpoint(nameOpenApiDocsYamlName, openApiYml)
      }
    }

    docs.toList
  }

  def isSwaggerEnabled: Boolean = {
    true //
  }
}
