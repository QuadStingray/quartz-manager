package dev.quadstingray.quartz.manager
import dev.quadstingray.quartz.manager.api.Server
import org.quartz.impl.StdSchedulerFactory
import scala.concurrent.duration.DurationInt
import scala.concurrent.Await
import sttp.client3.HttpClientSyncBackend

object TestAdditions {
  lazy val backend        = HttpClientSyncBackend()
  private lazy val server = new Server()

  private var isServerStarted = false

  def startServer(): Unit = {
    if (!isServerStarted) {
      StdSchedulerFactory.getDefaultScheduler.start()
      Await.result(server.startServer(), 30.seconds)
      isServerStarted = true
    }
  }

  def stopServer(): Unit = {
    if (isServerStarted) {
      StdSchedulerFactory.getDefaultScheduler.start()
      StdSchedulerFactory.getDefaultScheduler.clear()
      server.shutdown()
      isServerStarted = false
    }
  }

  def testServerHost: String = {
    s"http://${server.interface}:${server.port}"
  }

}
