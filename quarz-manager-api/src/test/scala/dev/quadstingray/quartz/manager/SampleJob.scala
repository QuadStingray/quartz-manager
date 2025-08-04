package dev.quadstingray.quartz.manager
import dev.quadstingray.quartz.job.HistoryJob
import java.lang.System.Logger.Level
import org.quartz.JobExecutionContext

class SampleJob extends HistoryJob {

  override def executeJob(context: JobExecutionContext): Unit = {
    addToLog("Hello Sample Job")
    addToLog("Error", Level.ERROR)
    addToLog("Bllu", Level.DEBUG)
  }

}
