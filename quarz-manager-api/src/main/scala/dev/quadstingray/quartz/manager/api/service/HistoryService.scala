package dev.quadstingray.quartz.manager.api.service
import com.github.blemale.scaffeine.Cache
import com.github.blemale.scaffeine.Scaffeine
import dev.quadstingray.quartz.manager.api.model.LogMessage
import dev.quadstingray.quartz.manager.api.model.LogRecord
import java.util.Date
import scala.collection.mutable.ListBuffer
import scala.jdk.DurationConverters._

object HistoryService {

  val cache: Cache[String, LogRecord] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(ConfigService.getDuration("dev.quadstingray.quarz-manager.history.max.age").toScala)
      .maximumSize(500)
      .build[String, LogRecord]()

  def addToLog(cacheKey: String, className: String, logMessage: LogMessage): Unit = {
    val logRecord = cache.getIfPresent(cacheKey).getOrElse(LogRecord(cacheKey, className, new Date(), ListBuffer.empty))
    logRecord.logMessages += logMessage
    cache.put(cacheKey, logRecord)
  }

}
