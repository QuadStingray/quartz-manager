package dev.quadstingray.quartz.manager.ui

import com.typesafe.scalalogging.LazyLogging
import dev.quadstingray.quartz.manager.api.HttpServer
import org.apache.pekko.http.scaladsl.server.Route
import scala.concurrent.Future
import sttp.capabilities.pekko.PekkoStreams
import sttp.capabilities.WebSockets
import sttp.tapir.files.staticResourcesGetServerEndpoint
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.stringToPath

object QuarzManagerUi extends LazyLogging {
  def uiRoutes: Route = {
    val resourcesEndpoint: ServerEndpoint[PekkoStreams with WebSockets, Future] = {
      staticResourcesGetServerEndpoint(stringToPath("ui"))(getClass.getClassLoader, s"META-INF/resources/webjars/quartz-manager-ui/")
    }
    HttpServer.defaultHttpServerInterpreter.toRoute(resourcesEndpoint)
  }
}
