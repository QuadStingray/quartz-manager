package dev.quadstingray.quartz.manager.api.model
import java.util.Date
import scala.collection.mutable.ListBuffer

case class LogRecord(
  id: String,
  className: String,
  date: Date,
  logMessages: ListBuffer[LogMessage],
  jobGroup: Option[String] = None,
  jobName: Option[String] = None
)
