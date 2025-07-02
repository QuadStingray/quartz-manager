package dev.quadstingray.quartz.job

import dev.quadstingray.quartz.manager.api.model.LogMessage
import dev.quadstingray.quartz.manager.api.service.HistoryService
import java.lang.System.Logger.Level
import java.util.Date
import java.util.UUID
import org.quartz.Job

abstract class HistoryJob extends Job {

  private lazy val id: String = UUID.randomUUID().toString

  def addToLog(logMessage: String, level: Level = Level.INFO): Unit = {
    HistoryService.addToLog(id, this.getClass.getName, LogMessage(new Date(), level.toString, logMessage))
  }

  addToLog(s"${getClass.getSimpleName} is instantiated", Level.DEBUG)

}
