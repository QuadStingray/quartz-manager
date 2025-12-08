package dev.quadstingray.quartz.manager.api.model
import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory
import java.net.InetAddress

case class SystemOverview(hostname: String, totalMemory: Long, maxMemory: Long, freeMemory: Long, systemCpuLoad: Double, processCpuLoad: Double)

object SystemOverview {
  def apply(): SystemOverview = {
    val osBean = ManagementFactory.getOperatingSystemMXBean.asInstanceOf[OperatingSystemMXBean]
    SystemOverview(
      InetAddress.getLocalHost.getHostName,
      Runtime.getRuntime.totalMemory(),
      Runtime.getRuntime.maxMemory(),
      Runtime.getRuntime.freeMemory(),
      osBean.getCpuLoad,
      osBean.getProcessCpuLoad
    )
  }
}
