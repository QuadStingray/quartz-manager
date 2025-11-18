package dev.quadstingray.quartz.manager.api
import dev.quadstingray.quartz.manager.client.api.SchedulerApi
import dev.quadstingray.quartz.manager.BaseServerSuite
import dev.quadstingray.quartz.manager.TestAdditions

class SchedulerApiSuite extends BaseServerSuite {

  test("Start Scheduler") {
    scheduler.standby()
    val response = TestAdditions.backend.send(SchedulerApi().startScheduler("", "admin", "pwd"))
    assert(response.isSuccess)
    assert(scheduler.isStarted, "Scheduler should be started")
  }

  test("Standby Scheduler") {
    scheduler.start()
    val response = TestAdditions.backend.send(SchedulerApi().standbyScheduler("", "admin", "pwd"))
    assert(response.isSuccess)
    assert(scheduler.isInStandbyMode, "Scheduler should be standby")
  }

  // other test could not run after called this test. scheduler is shutdown after this test and could not restart.
  test("Shutdown Scheduler".ignore) {
    scheduler.start()
    val response = TestAdditions.backend.send(SchedulerApi().shutdownScheduler("", "admin", "pwd")(waitForJobsToComplete = Some(true)))
    assert(response.isSuccess)
    assert(scheduler.isShutdown, "Scheduler should be shutdown")
  }

}
