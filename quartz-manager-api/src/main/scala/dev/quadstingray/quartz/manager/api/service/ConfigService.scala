package dev.quadstingray.quartz.manager.api.service

import com.typesafe.config._
import java.lang
import java.time.temporal.TemporalAmount
import java.time.Duration
import java.time.Period
import java.util
import java.util.concurrent.TimeUnit

object ConfigService {
  private lazy val config: Config = ConfigFactory.load()

  def entrySet(): util.Set[util.Map.Entry[String, ConfigValue]] = config.entrySet()

  def getIsNull(path: String): Boolean = config.getIsNull(path)

  def getBoolean(path: String): Boolean = config.getBoolean(path)

  def getNumber(path: String): Number = config.getNumber(path)

  def getInt(path: String): Int = config.getInt(path)

  def getLong(path: String): Long = config.getLong(path)

  def getDouble(path: String): Double = config.getDouble(path)

  def getString(path: String): String = config.getString(path)

  def getEnum[T <: Enum[T]](enumClass: Class[T], path: String): T = config.getEnum[T](enumClass, path)

  def getObject(path: String): ConfigObject = config.getObject(path)

  def getConfig(path: String): Config = config.getConfig(path)

  def getAnyRef(path: String): AnyRef = config.getAnyRef(path)

  def getValue(path: String): ConfigValue = config.getValue(path)

  def getBytes(path: String): lang.Long = config.getBytes(path)

  def getMemorySize(path: String): ConfigMemorySize = config.getMemorySize(path)

  def getMilliseconds(path: String): lang.Long = config.getMilliseconds(path)

  def getNanoseconds(path: String): lang.Long = config.getNanoseconds(path)

  def getDuration(path: String, unit: TimeUnit): Long = config.getDuration(path, unit)

  def getDuration(path: String): Duration = config.getDuration(path): Duration

  def getPeriod(path: String): Period = config.getPeriod(path)

  def getTemporal(path: String): TemporalAmount = config.getTemporal(path)

  def getList(path: String): ConfigList = config.getList(path)

  def getBooleanList(path: String): util.List[lang.Boolean] = config.getBooleanList(path)

  def getNumberList(path: String): util.List[Number] = config.getNumberList(path)

  def getIntList(path: String): util.List[Integer] = config.getIntList(path)

  def getLongList(path: String): util.List[lang.Long] = config.getLongList(path)

  def getDoubleList(path: String): util.List[lang.Double] = config.getDoubleList(path)

  def getStringList(path: String): util.List[String] = config.getStringList(path)

  def getEnumList[T <: Enum[T]](enumClass: Class[T], path: String): util.List[T] = config.getEnumList[T](enumClass, path)

  def getObjectList(path: String): util.List[_ <: ConfigObject] = config.getObjectList(path)

  def getConfigList(path: String): util.List[_ <: Config] = config.getConfigList(path)

  def getAnyRefList(path: String): util.List[_] = config.getAnyRefList(path: String)

  def getBytesList(path: String): util.List[lang.Long] = config.getBytesList(path)

  def getMemorySizeList(path: String): util.List[ConfigMemorySize] = config.getMemorySizeList(path)

  def getMillisecondsList(path: String): util.List[lang.Long] = config.getMillisecondsList(path)

  def getNanosecondsList(path: String): util.List[lang.Long] = config.getNanosecondsList(path)

  def getDurationList(path: String, unit: TimeUnit): util.List[lang.Long] = config.getDurationList(path, unit)

  def getDurationList(path: String): util.List[Duration] = config.getDurationList(path)

  def withOnlyPath(path: String): Config = config.withOnlyPath(path)

  def withoutPath(path: String): Config = config.withoutPath(path)

  def atPath(path: String): Config = config.atPath(path)

  def atKey(key: String): Config = config.atKey(key)

}
