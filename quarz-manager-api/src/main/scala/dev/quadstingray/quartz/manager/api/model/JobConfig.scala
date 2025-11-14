package dev.quadstingray.quartz.manager.api.model

case class JobConfig(
  name: String,
  className: String,
  description: Option[String],
  cronExpression: String,
  group: String = ModelConstants.jobDefaultGroup,
  priority: Int = ModelConstants.jobDefaultPriority,
  jobDataMap: Map[String, Any] = Map.empty
)
