package dev.quadstingray.quartz.manager
import org.quartz.impl.StdSchedulerFactory

trait BaseServerSuite extends munit.FunSuite {
  protected val scheduler = StdSchedulerFactory.getDefaultScheduler

  override def beforeAll(): Unit = {
    TestAdditions.startServer()
    resetDatabase()
  }

  override def afterAll(): Unit = {
    resetDatabase()
  }

  private def resetDatabase(): Unit = synchronized {}
}
