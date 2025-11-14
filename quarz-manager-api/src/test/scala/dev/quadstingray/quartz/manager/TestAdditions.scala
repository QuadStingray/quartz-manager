package dev.quadstingray.quartz.manager
import dev.quadstingray.quartz.manager.api.Server
import org.quartz.impl.StdSchedulerFactory
import sttp.client3.HttpClientSyncBackend

object TestAdditions {
  lazy val backend        = HttpClientSyncBackend()
  private lazy val server = new Server()

  private var isServerStarted = false

  def startServer(): Unit = {
    if (!isServerStarted) {
      StdSchedulerFactory.getDefaultScheduler.start()
      server.startServer()
      isServerStarted = true
    }
  }

  def testServerHost: String = {
    s"http://${server.interface}:${server.port}"
  }

}
