package dev.quadstingray.quartz.manager.api
import org.quartz.impl.StdSchedulerFactory

class BaseServerSuite extends munit.FunSuite {
  test("list users as admin") {
    StdSchedulerFactory.getDefaultScheduler.start()
    val server = new Server()
    server.startServer()
    while (true) {}
  }
}
