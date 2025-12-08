package dev.quadstingray.quartz.manager.api

import org.apache.pekko.actor.ActorSystem
import scala.concurrent.ExecutionContextExecutor

object ActorHandler {

  lazy val requestActorSystem: ActorSystem = ActorSystem("quartz-manager-api-requests")

  def requestExecutionContext: ExecutionContextExecutor = requestActorSystem.dispatcher

}
