package dev.quadstingray.quartz.manager
import org.quartz.Job
import org.quartz.JobExecutionContext

class SampleJob extends Job {

  override def execute(context: JobExecutionContext): Unit = {
    println("Hello Sample Job")
  }

}
