package dev.quadstingray.quartz.manager.api

import com.typesafe.scalalogging.LazyLogging
import dev.quadstingray.quartz.manager.api.routes.ApiDocsRoutes
import dev.quadstingray.quartz.manager.api.routes.AuthRoutes
import dev.quadstingray.quartz.manager.api.routes.HistoryRoutes
import dev.quadstingray.quartz.manager.api.routes.JobRoutes
import dev.quadstingray.quartz.manager.api.routes.SchedulerRoutes
import dev.quadstingray.quartz.manager.api.routes.SystemRoutes
import dev.quadstingray.quartz.manager.api.service.auth.AuthenticationService
import dev.quadstingray.quartz.manager.api.service.auth.DefaultAuthenticationService
import dev.quadstingray.quartz.manager.api.service.ClassGraphService
import dev.quadstingray.quartz.manager.api.service.ConfigService
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.server.Directives.reject
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.RouteConcatenation
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.Done
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Scheduler
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import sttp.capabilities.pekko.PekkoStreams
import sttp.capabilities.WebSockets
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter
import sttp.tapir.server.ServerEndpoint

class Server(
  classGraphService: ClassGraphService = new ClassGraphService(),
  authenticationService: AuthenticationService = DefaultAuthenticationService(BuildInfo.name, BuildInfo.version),
  scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler,
  httpServerInterpreter: PekkoHttpServerInterpreter = HttpServer.defaultHttpServerInterpreter
) extends LazyLogging
    with RouteConcatenation {

  val interface: String = ConfigService.getString("dev.quadstingray.quartz-manager.interface")
  val port: Int         = ConfigService.getInt("dev.quadstingray.quartz-manager.port")

  implicit private lazy val actorSystem: ActorSystem = ActorHandler.requestActorSystem
  implicit private lazy val ex: ExecutionContext     = ActorHandler.requestExecutionContext

  private lazy val preLoadedRoutes: ArrayBuffer[Route]                 = ArrayBuffer()
  private lazy val afterLoadedRoutes: ArrayBuffer[Route]               = ArrayBuffer()
  private lazy val beforeServerStartCallBacks: ArrayBuffer[() => Unit] = ArrayBuffer()
  private lazy val afterServerStartCallBacks: ArrayBuffer[() => Unit]  = ArrayBuffer()
  private lazy val serverShutdownCallBacks: ArrayBuffer[() => Unit]    = ArrayBuffer()
  private var shutdownRunning: Boolean                                 = false
  private var serverBinding: Http.ServerBinding                        = _

  private def serverEndpoints: List[ServerEndpoint[PekkoStreams with WebSockets, Future]] = {
    new AuthRoutes(authenticationService).endpoints ++
      new SystemRoutes(authenticationService, scheduler).endpoints ++
      new SchedulerRoutes(authenticationService, scheduler).endpoints ++
      new JobRoutes(authenticationService, classGraphService, scheduler).endpoints ++
      new HistoryRoutes(authenticationService).endpoints
  }

  private def routes: Route = {
    val apiDocsRoutes     = new ApiDocsRoutes(authenticationService.name, authenticationService.version)
    val internalEndPoints = apiDocsRoutes.addDocsRoutes(serverEndpoints) ++ serverEndpoints
    val allEndpoints = internalEndPoints.map(
      ep => httpServerInterpreter.toRoute(ep)
    )
    concat(allEndpoints: _*)
  }

  private def routeHandler(r: Route): Route = {
    preLoadedRoutes.foldLeft[Route](reject)(_ ~ _) ~ r ~ afterLoadedRoutes.foldLeft[Route](reject)(_ ~ _)
  }

  def startServer(): Future[Unit] = {
    while (shutdownRunning) {}
    beforeServerStartCallBacks.foreach(
      f => f()
    )
    Http()
      .newServerAt(interface, port)
      .bindFlow(routeHandler(routes))
      .map(
        binding => {
          logger.warn("init server with interface: %s at port: %s".format(interface, port))
          if (ConfigService.getBoolean("dev.quadstingray.quartz-manager.open.api.enabled")) {
            logger.warn("For Swagger go to: http://%s:%s/docs".format(interface, port))
          }
          afterServerStartCallBacks.foreach(
            f => f()
          )
          serverBinding = binding
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
    if (!shutdownRunning) {
      shutdownRunning = true
      serverShutdownCallBacks.foreach(
        f => f()
      )
      Await.result(serverBinding.unbind(), Duration.Inf)
      Await.result(serverBinding.terminate(1.millis), Duration.Inf)
      shutdownRunning = false
    }
  }

}
