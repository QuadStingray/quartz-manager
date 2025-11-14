package dev.quadstingray.quartz.manager.api.jobs

import dev.quadstingray.quartz.job.HistoryJob
import org.quartz.JobExecutionContext

import java.lang.System.Logger.Level

class SampleJob extends HistoryJob {

  override def executeJob(context: JobExecutionContext): Unit = {
    addToLog("Hello Sample Job")
    addToLog("Error", Level.ERROR)
    addToLog("Bllu", Level.DEBUG)
  }

}
