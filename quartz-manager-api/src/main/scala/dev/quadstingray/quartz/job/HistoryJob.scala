package dev.quadstingray.quartz.job

import dev.quadstingray.quartz.manager.api.model.LogMessage
import dev.quadstingray.quartz.manager.api.service.HistoryService
import java.lang.System.Logger.Level
import java.util.Date
import java.util.UUID
import org.quartz.Job
import org.quartz.JobDetail
import org.quartz.JobExecutionContext

abstract class HistoryJob extends Job {

  private lazy val id: String      = UUID.randomUUID().toString
  private var jobDetail: JobDetail = _

  def addToLog(logMessage: String, level: Level = Level.INFO): Unit = {
    HistoryService.addToLog(id, jobDetail, LogMessage(new Date(), level.toString, logMessage), this.getClass.getName)
  }

  override def execute(context: JobExecutionContext): Unit = {
    jobDetail = context.getJobDetail
    addToLog(s"Job `${this.getClass.getSimpleName}` execution started", Level.INFO)
    executeJob(context)
    addToLog(s"Job `${this.getClass.getSimpleName}` execution finished", Level.INFO)
  }

  def executeJob(context: JobExecutionContext): Unit

  addToLog(s"${getClass.getSimpleName} is instantiated", Level.DEBUG)

}
