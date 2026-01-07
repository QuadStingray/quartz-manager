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
    scheduler.start()
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
    val response = TestAdditions.backend.send(HistoryApi().historyList("", "admin", "pwd")())
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

  test("Job History List - sort by date descending") {
    val response = TestAdditions.backend.send(HistoryApi().historyList("", "admin", "pwd")(sort = Some("-date")))
    assert(response.isSuccess)
    val logRecords = response.body.getOrElse(List.empty)
    assert(logRecords.nonEmpty)
  }

  test("Job History List - query by single parameter") {
    val response = TestAdditions.backend.send(HistoryApi().historyList("", "admin", "pwd")(query = Some("jobGroup:testGroup")))
    assert(response.isSuccess)
    val logRecords = response.body.getOrElse(List.empty)
    assert(logRecords.nonEmpty)
    assert(logRecords.forall(_.jobGroup.contains("testGroup")))
  }

  test("Job History List - query with two parameters") {
    val response = TestAdditions.backend.send(
      HistoryApi().historyList("", "admin", "pwd")(query = Some("jobGroup:testGroup AND className:dev.quadstingray.quartz.manager.SampleJob"))
    )
    assert(response.isSuccess)
    val logRecords = response.body.getOrElse(List.empty)
    assert(logRecords.nonEmpty)
    assert(
      logRecords.forall(
        rec => rec.jobGroup.contains("testGroup") && rec.className == "dev.quadstingray.quartz.manager.SampleJob"
      )
    )
  }

  test("Job History List - query with OR operator") {
    val response = TestAdditions.backend.send(HistoryApi().historyList("", "admin", "pwd")(query = Some("jobName:jobForTesting OR jobName:nonExistentJob")))
    assert(response.isSuccess)
    val logRecords = response.body.getOrElse(List.empty)
    assert(logRecords.nonEmpty)
    assert(logRecords.forall(_.jobName.contains("jobForTesting")))
  }

  test("Job History List - pagination with page 1 and rowsPerPage 1") {
    val response = TestAdditions.backend.send(HistoryApi().historyList("", "admin", "pwd")(rowsPerPage = Some(1), page = Some(1)))
    assert(response.isSuccess)
    val logRecords = response.body.getOrElse(List.empty)
    assertEquals(logRecords.size, 1)
  }

  test("Job History List - pagination page 2") {
    val allResponse = TestAdditions.backend.send(HistoryApi().historyList("", "admin", "pwd")())
    val allRecords = allResponse.body.getOrElse(List.empty)

    if (allRecords.size > 1) {
      val page2Response = TestAdditions.backend.send(HistoryApi().historyList("", "admin", "pwd")(rowsPerPage = Some(1), page = Some(2)))
      assert(page2Response.isSuccess)
      val page2Records = page2Response.body.getOrElse(List.empty)
      assertEquals(page2Records.size, 1)
      assert(page2Records.head.id != allRecords.head.id)
    }
  }

}
