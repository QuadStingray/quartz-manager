package dev.quadstingray.quartz.manager.api
import dev.quadstingray.quartz.manager.client.api.SchedulerApi
import dev.quadstingray.quartz.manager.BaseServerSuite
import dev.quadstingray.quartz.manager.TestAdditions
import org.quartz.impl.StdSchedulerFactory

class SchedulerApiSuite extends BaseServerSuite {

  test("Start Scheduler") {
    scheduler.standby()
    val response = TestAdditions.backend.send(SchedulerApi().startScheduler("admin", "admin", None))
    assert(response.isSuccess)
    assert(scheduler.isStarted, "Scheduler should be started")
  }

  test("Standby Scheduler") {
    scheduler.start()
    val response = TestAdditions.backend.send(SchedulerApi().standbyScheduler("admin", "admin", None))
    assert(response.isSuccess)
    assert(scheduler.isInStandbyMode, "Scheduler should be standby")
  }

  test("Shutdown Scheduler") {
    scheduler.start()
    val response = TestAdditions.backend.send(SchedulerApi().shutdownScheduler("admin", "admin", None)(waitForJobsToComplete = Some(true)))
    assert(response.isSuccess)
    assert(scheduler.isShutdown, "Scheduler should be shutdown")
  }

}
