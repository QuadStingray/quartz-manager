package dev.quadstingray.quartz.manager.api.model
import org.quartz.Scheduler

object Status extends Enumeration {
  type Status = Status.Value
  val Started  = Value("Started")
  val Standby  = Value("Standby")
  val Shutdown = Value("Shutdown")
  val Unknown  = Value("Unknown")

  def fromString(s: String): Status.Value = {
    if (s.equalsIgnoreCase(Status.Started.toString)) {
      Status.Started
    }
    else if (s.equalsIgnoreCase(Status.Standby.toString)) {
      Status.Standby
    }
    else if (s.equalsIgnoreCase(Status.Shutdown.toString)) {
      Status.Shutdown
    }
    else {
      Unknown
    }
  }

  def fromScheduler(scheduler: Scheduler) = {
    val status = if (scheduler.isInStandbyMode) {
      Status.Standby
    }
    else if (scheduler.isStarted) {
      Status.Started
    }
    else if (scheduler.isShutdown) {
      Status.Shutdown
    }
    else {
      Status.Unknown
    }
    status
  }
}
