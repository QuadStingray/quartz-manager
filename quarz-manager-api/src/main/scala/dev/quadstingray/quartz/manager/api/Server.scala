package dev.quadstingray.quartz.manager.api

import com.typesafe.scalalogging.LazyLogging
import dev.quadstingray.quartz.manager.api.routes.docs.ApiDocsRoutes
import dev.quadstingray.quartz.manager.api.routes.JobRoutes
import dev.quadstingray.quartz.manager.api.service.ClassGraphService
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.server.Directives.reject
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.RouteConcatenation
import org.apache.pekko.http.scaladsl.Http
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Scheduler
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import sttp.capabilities.pekko.PekkoStreams
import sttp.capabilities.WebSockets
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter
import sttp.tapir.server.ServerEndpoint

class Server(
  classGraphService: ClassGraphService = new ClassGraphService(),
  scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler,
  httpServerInterpreter: PekkoHttpServerInterpreter = HttpServer.defaultHttpServerInterpreter
) extends LazyLogging
    with RouteConcatenation {

  implicit private lazy val actorSystem: ActorSystem = ActorHandler.requestActorSystem
  implicit private lazy val ex: ExecutionContext     = ActorHandler.requestExecutionContext

  private lazy val preLoadedRoutes: ArrayBuffer[Route]                 = ArrayBuffer()
  private lazy val afterLoadedRoutes: ArrayBuffer[Route]               = ArrayBuffer()
  private lazy val beforeServerStartCallBacks: ArrayBuffer[() => Unit] = ArrayBuffer()
  private lazy val afterServerStartCallBacks: ArrayBuffer[() => Unit]  = ArrayBuffer()
  private lazy val serverShutdownCallBacks: ArrayBuffer[() => Unit]    = ArrayBuffer()
  private var shutdownStarted: Boolean                                 = false

  private def serverEndpoints: List[ServerEndpoint[PekkoStreams with WebSockets, Future]] = {
    new JobRoutes(classGraphService, scheduler).endpoints
  }

  private def routes: Route = {
    val internalEndPoints = serverEndpoints ++ ApiDocsRoutes.addDocsRoutes(serverEndpoints)
    val allEndpoints = internalEndPoints.map(
      ep => httpServerInterpreter.toRoute(ep)
    )
    concat(allEndpoints: _*)
  }

  private def routeHandler(r: Route): Route = {
    preLoadedRoutes.foldLeft[Route](reject)(_ ~ _) ~ r ~ afterLoadedRoutes.foldLeft[Route](reject)(_ ~ _)
  }

  val interface: String = "localhost"
  val port: Int         = 8080

  def startServer(): Future[Unit] = {
    beforeServerStartCallBacks.foreach(
      f => f()
    )
    Http()
      .newServerAt(interface, port)
      .bindFlow(routeHandler(routes))
      .map(
        serverBinding => {
          logger.warn("init server with interface: %s at port: %s".format(interface, port))
          if (ApiDocsRoutes.isSwaggerEnabled) {
            logger.warn("For Swagger go to: http://%s:%s/docs".format(interface, port))
          }
          afterServerStartCallBacks.foreach(
            f => f()
          )
          serverBinding
        }
      )
  }

  def registerBeforeStartCallBack(f: () => Unit): Unit = {
    beforeServerStartCallBacks.addOne(f)
  }

  def registerAfterStartCallBack(f: () => Unit): Unit = {
    afterServerStartCallBacks.addOne(f)
  }

  def registerPreLoadedRoutes(r: Route): Unit = {
    preLoadedRoutes.addOne(r)
  }

  def registerAfterLoadedRoutes(r: Route): Unit = {
    afterLoadedRoutes.addOne(r)
  }

  def registerServerShutdownCallBacks(f: () => Unit): Unit = {
    serverShutdownCallBacks.addOne(f)
  }

  def shutdown(): Unit = {
    if (!shutdownStarted) {
      shutdownStarted = true
      serverShutdownCallBacks.foreach(
        f => f()
      )
      ActorHandler.requestActorSystem.terminate()
      actorSystem.terminate()
    }
  }
}
