package dev.quadstingray.quartz.manager.api.service
import com.github.blemale.scaffeine.Cache
import com.github.blemale.scaffeine.Scaffeine
import dev.quadstingray.quartz.manager.api.model.LogMessage
import dev.quadstingray.quartz.manager.api.model.LogRecord
import java.util.Date
import org.quartz.JobDetail
import scala.collection.mutable.ListBuffer
import scala.jdk.DurationConverters._

object HistoryService {

  val cache: Cache[String, LogRecord] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(ConfigService.getDuration("dev.quadstingray.quartz-manager.history.max.age").toScala)
      .maximumSize(500)
      .build[String, LogRecord]()

  def addToLog(cacheKey: String, jobDetail: JobDetail, logMessage: LogMessage, defaultClassName: String): Unit = {
    var logRecord = cache
      .getIfPresent(cacheKey)
      .getOrElse(LogRecord(cacheKey, Option(jobDetail).map(_.getJobClass.getName).getOrElse(defaultClassName), new Date(), ListBuffer.empty))
    logRecord.logMessages += logMessage
    Option(jobDetail).foreach(
      jD => logRecord = logRecord.copy(jobGroup = Option(jD.getKey.getGroup), jobName = Option(jD.getKey.getName))
    )
    cache.put(cacheKey, logRecord)
  }

}
