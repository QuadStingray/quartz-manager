package dev.quadstingray.quartz.manager.api.model
import org.quartz.Scheduler

case class Overview(system: SystemOverview, scheduler: SchedulerInformation)

object Overview {
  def apply(scheduler: Scheduler): Overview = Overview(SystemOverview(), SchedulerInformation(scheduler))
}
