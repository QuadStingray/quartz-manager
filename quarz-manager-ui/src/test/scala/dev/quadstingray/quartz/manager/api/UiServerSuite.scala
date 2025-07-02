package dev.quadstingray.quartz.manager.api
import dev.quadstingray.quartz.manager.ui.QuarzManagerUi
import scala.concurrent.Future
import sttp.capabilities.pekko.PekkoStreams
import sttp.capabilities.WebSockets
import sttp.tapir.emptyInput
import sttp.tapir.files.staticResourcesGetServerEndpoint
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.stringToPath

class UiServerSuite extends munit.FunSuite {
  test("list users as admin") {
    val server = new Server()
    server.registerAfterLoadedRoutes(QuarzManagerUi.uiRoutes)
    server.startServer()
    while (true) {}
  }
}
