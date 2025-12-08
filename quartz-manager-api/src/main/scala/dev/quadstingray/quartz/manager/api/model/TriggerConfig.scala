package dev.quadstingray.quartz.manager.api.model

import java.util.Date

case class TriggerConfig(className: String, priority: Int, jobDataMap: Map[String, String])
