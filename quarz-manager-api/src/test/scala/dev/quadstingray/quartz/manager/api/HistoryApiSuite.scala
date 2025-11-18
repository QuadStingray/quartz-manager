package dev.quadstingray.quartz.manager.api

import dev.quadstingray.quartz.manager.client.api.HistoryApi
import dev.quadstingray.quartz.manager.client.api.JobsApi
import dev.quadstingray.quartz.manager.client.model.JobConfig
import dev.quadstingray.quartz.manager.BaseServerSuite
import dev.quadstingray.quartz.manager.TestAdditions
import scala.concurrent.duration.DurationInt

class HistoryApiSuite extends BaseServerSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    if (!scheduler.isStarted) {
      scheduler.start()
    }
    val registerJobResponse = TestAdditions.backend.send(
      JobsApi().registerJob("", "admin", "pwd")(
        JobConfig(
          name = "jobForTesting",
          className = "dev.quadstingray.quartz.manager.SampleJob",
          cronExpression = "0 0 0 ? * * 2088",
          group = "testGroup",
          priority = 0,
          jobDataMap = Map.empty
        )
      )
    )
    assert(registerJobResponse.isSuccess)
    val triggerJobResponse = TestAdditions.backend.send(JobsApi().executeJob("", "admin", "pwd")("testGroup", "jobForTesting", Map.empty))
    assert(triggerJobResponse.isSuccess)
    Thread.sleep(10.seconds.toMillis)
  }

  test("Job History List") {
    scheduler.standby()
    val response = TestAdditions.backend.send(HistoryApi().historyList("", "admin", "pwd"))
    assert(response.isSuccess)
    val logRecords = response.body.getOrElse(List.empty)
    assert(logRecords.nonEmpty)
    val logRec = logRecords.head
    assert(logRec.logMessages.isDefined, "Log message should not be empty")
    val logMessageBuffer = logRec.logMessages.get
    assertEquals(logMessageBuffer.size, 6)
    assertEquals(logMessageBuffer.head.level, "DEBUG")
    assertEquals(logMessageBuffer.head.logMessage, "SampleJob is instantiated")
    assertEquals(logMessageBuffer.last.level, "INFO")
    assertEquals(logMessageBuffer.last.logMessage, "Job `SampleJob` execution finished")
  }

}
