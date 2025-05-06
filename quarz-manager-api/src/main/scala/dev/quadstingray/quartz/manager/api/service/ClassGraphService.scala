package dev.quadstingray.quartz.manager.api.service
import com.typesafe.scalalogging.LazyLogging
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import scala.jdk.CollectionConverters._
import scala.reflect.runtime.universe.runtimeMirror
import scala.util.Try

class ClassGraphService extends LazyLogging {
  var scanResult: ScanResult = _

  private lazy val classGraph: ClassGraph = {
    val cG = new ClassGraph()
      .enableClassInfo()
      .disableModuleScanning()
      .addClassLoader(ClassLoader.getSystemClassLoader)
    cG
  }

  private def scan(): ScanResult = {
    classGraph.scan()
  }

  private def getScanResult: ScanResult = {
    if (scanResult == null) {
      scanClassPath()
    }
    scanResult
  }

  private def scanClassPath(): Unit = {
    scanResult = scan()
  }

  def getSubClassesList[T <: Any](clazz: Class[T]): List[ClassInfo] = {
    logger.trace(s"Count scanned classes ${getScanResult.getAllClasses.size()}")
    val classInfoList    = getScanResult.getClassesImplementing(clazz).asScala.toList
    val subClassInfoList = getScanResult.getSubclasses(clazz).asScala.toList
    logger.trace(s"Found ${classInfoList.size} classes implementing and ${subClassInfoList.size} subclasses for ${clazz.getName}")
    classInfoList ++ subClassInfoList
  }

  def registerClassLoaders[T <: Any](clazz: Class[T]): Unit = {
    registerClassLoaders(clazz.getClassLoader)
  }

  def registerClassLoaders(classLoader: ClassLoader): Unit = {
    classGraph.addClassLoader(classLoader)
    if (classLoader.getParent != null) {
      registerClassLoaders(classLoader.getParent)
      scanClassPath()
    }
  }

  def getClassByName(className: String): Class[_] = {
    Try(getScanResult.getClassInfo(className).loadClass()).toOption.getOrElse(throw new ClassNotFoundException())
  }

}
