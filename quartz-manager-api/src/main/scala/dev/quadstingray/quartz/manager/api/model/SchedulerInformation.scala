package dev.quadstingray.quartz.manager.api.model

import org.quartz.Scheduler

case class SchedulerInformation(
  id: String,
  name: String,
  version: String,
  schedulerClass: String,
  jobStoreClass: String,
  status: Status.Value,
  currentlyExecutingJobs: Int,
  threadPoolSize: Int
)

object SchedulerInformation {
  def apply(scheduler: Scheduler): SchedulerInformation = {
    SchedulerInformation(
      scheduler.getSchedulerInstanceId,
      scheduler.getSchedulerName,
      scheduler.getMetaData.getVersion,
      scheduler.getMetaData.getSchedulerClass.getName,
      scheduler.getMetaData.getJobStoreClass.getName,
      Status.fromScheduler(scheduler),
      scheduler.getCurrentlyExecutingJobs.size,
      scheduler.getMetaData.getThreadPoolSize
    )
  }
}
