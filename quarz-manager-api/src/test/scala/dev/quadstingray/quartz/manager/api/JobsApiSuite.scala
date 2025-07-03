package dev.quadstingray.quartz.manager.api

import dev.quadstingray.quartz.manager.client.api.JobsApi
import dev.quadstingray.quartz.manager.client.model.JobConfig
import dev.quadstingray.quartz.manager.client.model.JobInformation
import dev.quadstingray.quartz.manager.BaseServerSuite
import dev.quadstingray.quartz.manager.TestAdditions
import org.joda.time.DateTime

class JobsApiSuite extends BaseServerSuite {

  var jobsRegistered: Int = 1

  override def beforeAll(): Unit = {
    super.beforeAll()
    val response = TestAdditions.backend.send(
      JobsApi().registerJob("admin", "admin", None)(
        JobConfig(
          name = "jobForTesting",
          className = "dev.quadstingray.quartz.manager.SampleJob",
          cronExpression = "0 0 0 ? * * 2088",
          group = "testGroup",
          priority = 0
        )
      )
    )
    assert(response.isSuccess)
  }

  test("List all possible jobs") {
    val response = TestAdditions.backend.send(JobsApi().possibleJobsList("admin", "admin", None))
    assert(response.isSuccess)
    val value = response.body.getOrElse {
      throw new Exception(response.body.left.get.getMessage)
    }
    assertEquals(value, List("dev.quadstingray.quartz.manager.SampleJob", "org.quartz.plugins.xml.FileScanJob"))
  }

  test("Register a new job") {
    val response = TestAdditions.backend.send(
      JobsApi().registerJob("admin", "admin", None)(
        JobConfig(
          name = "mySampleJob",
          className = "dev.quadstingray.quartz.manager.SampleJob",
          cronExpression = "0 0 0 ? * * 2099",
          group = "testGroup",
          priority = 0
        )
      )
    )
    assert(response.isSuccess)
    jobsRegistered += 1
    val value = response.body.getOrElse {
      throw new Exception(response.body.left.get.getMessage)
    }
    assertEquals(
      value,
      JobInformation(
        name = "mySampleJob",
        group = "testGroup",
        jobClassName = "dev.quadstingray.quartz.manager.SampleJob",
        cronExpression = "0 0 0 ? * * 2099",
        priority = 0,
        description = None,
        lastScheduledFireTime = None,
        nextScheduledFireTime = Some(new DateTime("2099-01-01T00:00:00.000+01:00")),
        scheduleInformation = None
      )
    )
  }

  test("Register a job with invalid cron expression".ignore) {
    val response = TestAdditions.backend.send(
      JobsApi().registerJob("admin", "admin", None)(
        JobConfig(
          name = "invalidCronJob",
          className = "dev.quadstingray.quartz.manager.SampleJob",
          cronExpression = "invalid cron expression",
          group = "testGroup",
          priority = 0
        )
      )
    )
    assert(response.isClientError)
  }

  test("List all registered jobs") {
    val response = TestAdditions.backend.send(JobsApi().jobsList("admin", "admin", None))
    assert(response.isSuccess)
    val value = response.body.getOrElse {
      throw new Exception(response.body.left.get.getMessage)
    }
    assertEquals(value.size, jobsRegistered)
    assert(value.exists(_.name == "jobForTesting"))
  }

  test("Update an existing job") {
    val response = TestAdditions.backend.send(
      JobsApi().updateJob("admin", "admin", None)(
        "testGroup",
        "jobForTesting",
        JobConfig(
          name = "jobForTesting",
          className = "dev.quadstingray.quartz.manager.SampleJob",
          cronExpression = "0 0 0 ? * * 2077",
          group = "testGroup",
          priority = 0
        )
      )
    )
    assert(response.isSuccess)
    val value = response.body.getOrElse {
      throw new Exception(response.body.left.get.getMessage)
    }
    assertEquals(
      value,
      JobInformation(
        name = "jobForTesting",
        group = "testGroup",
        jobClassName = "dev.quadstingray.quartz.manager.SampleJob",
        cronExpression = "0 0 0 ? * * 2077",
        priority = 0,
        description = None,
        lastScheduledFireTime = None,
        nextScheduledFireTime = Some(new DateTime("2077-01-01T00:00:00.000+01:00")),
        scheduleInformation = None
      )
    )
  }

  test("Trigger an existing job") {
    val request  = JobsApi().executeJob("admin", "admin", None)("testGroup", "jobForTesting", Map.empty)
    val response = TestAdditions.backend.send(request)
    assert(response.isSuccess)
  }

  test("Delete an existing job") {
    val response = TestAdditions.backend.send(JobsApi().deleteJob("admin", "admin", None)("testGroup", "jobForTesting"))
    assert(response.isSuccess)
    jobsRegistered -= 1
    val listResponse = TestAdditions.backend.send(JobsApi().jobsList("admin", "admin", None))
    assert(listResponse.isSuccess)
    val value = listResponse.body.getOrElse {
      throw new Exception(listResponse.body.left.get.getMessage)
    }
    assertEquals(value.size, jobsRegistered)
    assert(!value.exists(_.name == "jobForTesting"))
  }

}
