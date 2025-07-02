package dev.quadstingray.quartz.manager.api.model
import java.lang.System.Logger.Level
import java.util.Date

case class LogMessage(date: Date, level: String, logMessage: String)
