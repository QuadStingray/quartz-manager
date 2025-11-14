package dev.quadstingray.quartz.manager.api

import dev.quadstingray.quartz.manager.api.model.JobConfig
import dev.quadstingray.quartz.manager.api.service.{ClassGraphService, JobSchedulerService}
import dev.quadstingray.quartz.manager.ui.QuarzManagerUi
import org.quartz.impl.StdSchedulerFactory

object TestServer extends App {

  private lazy val server = new Server()
  server.registerAfterLoadedRoutes(QuarzManagerUi.uiRoutes)
  val jobSchedulerService = new JobSchedulerService(new ClassGraphService(), StdSchedulerFactory.getDefaultScheduler)

  (1 to 10).foreach(
    i => jobSchedulerService.scheduleJob(JobConfig("testJob" + i, "dev.quadstingray.quartz.manager.api.jobs.SampleJob", None, "0 0 0 ? * * 2088"))
  )

  StdSchedulerFactory.getDefaultScheduler.start()
  server.startServer()

}
