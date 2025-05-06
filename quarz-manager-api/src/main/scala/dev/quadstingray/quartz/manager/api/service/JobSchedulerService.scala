package dev.quadstingray.quartz.manager.api.service
import com.typesafe.scalalogging.LazyLogging
import dev.quadstingray.quartz.manager.api.model.JobConfig
import dev.quadstingray.quartz.manager.api.model.JobInformation
import java.util.Date
import org.quartz._
import org.quartz.impl.matchers.GroupMatcher
import org.quartz.JobBuilder._
import org.quartz.TriggerBuilder._
import scala.jdk.CollectionConverters._
import scala.jdk.CollectionConverters.ListHasAsScala

class JobSchedulerService(classGraphService: ClassGraphService, scheduler: Scheduler) extends LazyLogging {

  def executeJob(jobGroup: String, jobName: String, jobDataMap: Map[String, Any]): Unit = {
    if (!scheduler.isStarted) {
      logger.warn("Scheduler is not started job is added to queue")
    }
    scheduler.triggerJob(new JobKey(jobName, jobGroup), new JobDataMap(jobDataMap.asJava))
  }

  def removeJobFromScheduler(jobGroup: String, jobName: String): Unit = {
    getTriggerList(jobGroup, jobName).foreach(
      trigger => scheduler.unscheduleJob(trigger.getKey)
    )
    scheduler.deleteJob(new JobKey(jobName, jobGroup))
  }

  def getTriggerList(jobGroup: String, jobName: String): List[Trigger] = {
    try {
      val trigger = scheduler.getTriggersOfJob(new JobKey(jobName, jobGroup)).asScala
      trigger.toList
    }
    catch {
      case _: Exception =>
        List()
    }
  }

  def scheduleJob(jobConfig: JobConfig): JobInformation = {
    var internalJobName = ""
    try {
      val jobClass: Class[_ <: Job] = classGraphService.getClassByName(jobConfig.className).asInstanceOf[Class[Job]]
      internalJobName = if (jobConfig.name.trim.equalsIgnoreCase("")) {
        jobClass.getSimpleName
      }
      else {
        jobConfig.name
      }
      val job = newJob(jobClass).withIdentity(internalJobName, jobConfig.group).build
      val trigger = newTrigger()
        .withIdentity(s"${internalJobName}Trigger", jobConfig.group)
        .withSchedule(CronScheduleBuilder.cronSchedule(jobConfig.cronExpression))
        .withPriority(jobConfig.priority)
        .forJob(job)
        .build()
      scheduler.scheduleJob(job, trigger)
      getJobInfo(jobConfig.group, internalJobName)
    }
    catch {
      case t: Throwable =>
        logger.error(s"job $internalJobName for group ${jobConfig.group} class (${jobConfig.className}) not found ")
        throw t
    }
  }

  def getJobInfo(jobGroup: String, jobName: String): JobInformation = {
    val jobDetail = scheduler.getJobDetail(new JobKey(jobName, jobGroup))
    convertJobDetailToJobInformation(jobDetail)
  }

  private def convertJobDetailToJobInformation(jobDetail: JobDetail): JobInformation = {
    val jobGroup                     = jobDetail.getKey.getGroup
    val jobName                      = jobDetail.getKey.getName
    val schedulerTriggerList         = getTriggerList(jobDetail.getKey.getGroup, jobDetail.getKey.getName)
    var nextFireTime: Option[Date]   = None
    var lastFireTime: Option[Date]   = None
    var scheduleInfo: Option[String] = None
    var priority: Int                = Int.MaxValue
    var triggerCron: String          = ""
    if (schedulerTriggerList.nonEmpty) {
      nextFireTime = Option(schedulerTriggerList.map(_.getNextFireTime).min)
      lastFireTime = Option(schedulerTriggerList.map(_.getPreviousFireTime).max)
      priority = schedulerTriggerList.map(_.getPriority).min
      triggerCron = schedulerTriggerList.filter(_.isInstanceOf[CronTrigger]).map(_.asInstanceOf[CronTrigger].getCronExpression).headOption.getOrElse("")
    }
    else {
      scheduleInfo = Some(s"Job `${jobName}` in group `${jobGroup}` is not scheduled.")
    }
    JobInformation(
      jobName,
      jobGroup,
      jobDetail.getJobClass.getName,
      Option(jobDetail.getDescription).filterNot(_.trim.isEmpty),
      triggerCron,
      priority,
      lastFireTime,
      nextFireTime,
      scheduleInfo
    )
  }

  def jobsList(): List[JobInformation] = {
    val jobList = scheduler.getJobGroupNames.asScala
      .filter(
        s => Option(s).nonEmpty
      )
      .flatMap(
        jobGroup => {
          scheduler
            .getJobKeys(GroupMatcher.jobGroupEquals(jobGroup))
            .asScala
            .map(
              jobKey => {
                val jobDetail = scheduler.getJobDetail(jobKey)
                convertJobDetailToJobInformation(jobDetail)
              }
            )
        }
      )
    jobList.toList
  }
}
