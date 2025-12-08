package dev.quadstingray.quartz.manager.api.routes

import dev.quadstingray.quartz.manager.api.json.CirceSchema
import dev.quadstingray.quartz.manager.api.service.ConfigService
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.capabilities.pekko.PekkoStreams
import sttp.capabilities.WebSockets
import sttp.model.Method
import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.swagger.SwaggerUIOptions

class ApiDocsRoutes(apiName: String, apiVersion: String) extends CirceSchema with SchemaDerivation {
  val nameOpenApiDocsYamlName = ConfigService.getString("dev.quadstingray.quartz-manager.open.api.yaml")

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

    if (isOpenApiEnabled) {
      val openApiDocs = OpenAPIDocsInterpreter().toOpenAPI(serverEndpoints.map(_.endpoint), apiName, apiVersion)

      val openApiYml: String = openApiDocs.toYaml

      if (isSwaggerEnabled) {
        val swaggerUIRoute =
          SwaggerUI[Future](
            openApiYml,
            SwaggerUIOptions(
              List(ConfigService.getString("dev.quadstingray.quartz-manager.open.api.path")),
              nameOpenApiDocsYamlName,
              List(),
              useRelativePaths = true,
              showExtensions = false,
              None,
              None
            )
          )
        docs ++= swaggerUIRoute
      }
      else {
        docs += docsYamlEndpoint(nameOpenApiDocsYamlName, openApiYml)
      }
    }

    docs.toList
  }

  def isOpenApiEnabled: Boolean = {
    ConfigService.getBoolean("dev.quadstingray.quartz-manager.open.api.enabled")
  }

  def isSwaggerEnabled: Boolean = {
    ConfigService.getBoolean("dev.quadstingray.quartz-manager.swagger.enabled")
  }
}
