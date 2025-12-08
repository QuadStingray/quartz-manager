package dev.quadstingray.quartz.manager
import org.quartz.impl.StdSchedulerFactory

trait BaseServerSuite extends munit.FunSuite {
  protected val scheduler = StdSchedulerFactory.getDefaultScheduler

  override def beforeAll(): Unit = {
    TestAdditions.startServer()
  }

  override def afterAll(): Unit = {
    TestAdditions.stopServer()
  }

}
