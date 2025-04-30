package dev.quadstingray.quartz.manager.api

class BaseServerSuite extends munit.FunSuite {
  test("list users as admin") {
    val server = new Server()
    server.startServer()
    while (true) {}
  }
}
