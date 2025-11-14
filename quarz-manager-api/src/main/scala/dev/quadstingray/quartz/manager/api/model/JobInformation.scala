package dev.quadstingray.quartz.manager.api.model

import java.util.Date

case class JobInformation(
  name: String,
  group: String,
  jobClassName: String,
  description: Option[String],
  cronExpression: String,
  priority: Int,
  jobDataMap: Map[String, Any],
  lastScheduledFireTime: Option[Date],
  nextScheduledFireTime: Option[Date],
  scheduleInformation: Option[String]
)
