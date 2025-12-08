# Quartz Manager

A REST API and Web UI for managing Quartz Scheduler jobs, built with [Tapir](https://tapir.softwaremill.com/en/latest/) and [Apache Pekko HTTP](https://pekko.apache.org/docs/pekko-http/current/).

## Overview

Quartz Manager provides a comprehensive solution for managing Quartz Scheduler jobs through a REST API and web interface. The project consists of two main modules:

- **quartz-manager-api**: REST API for job scheduling and management
- **quartz-manager-ui**: Web interface packaged as a WebJar

## Installation

The project artifacts are available on Maven Central:

### Maven

```xml
<!-- API -->
<dependency>
    <groupId>dev.quadstingray</groupId>
    <artifactId>quartz-manager-api_2.13</artifactId>
    <version>0.7.0</version>
</dependency>

<!-- Web UI (WebJar) -->
<dependency>
    <groupId>dev.quadstingray</groupId>
    <artifactId>quartz-manager-ui_2.13</artifactId>
    <version>0.7.0</version>
</dependency>
```

### SBT

```scala
// API
libraryDependencies += "dev.quadstingray" %% "quartz-manager-api" % "0.7.0"

// Web UI (WebJar)
libraryDependencies += "dev.quadstingray" %% "quartz-manager-ui" % "0.7.0"
```

**Maven Central Links:**
- [quartz-manager-api](https://mvnrepository.com/artifact/dev.quadstingray/quartz-manager-api)
- [quartz-manager-ui](https://mvnrepository.com/artifact/dev.quadstingray/quartz-manager-ui)

## Scala Version Support

The project supports:
- Scala 2.13.x
- Scala 3.7.x

## Features

- Schedule and manage Quartz jobs via REST API
- Interactive web interface for job management
- OpenAPI/Swagger documentation
- Authentication support (Bearer Token & Basic Auth)
- Job execution history tracking
- System and scheduler information endpoints
- Built on Tapir for type-safe API definitions
- Powered by Apache Pekko HTTP

## Quick Start

### Basic Server Setup

```scala
import dev.quadstingray.quartz.manager.api.Server
import dev.quadstingray.quartz.manager.api.service.{ClassGraphService, JobSchedulerService}
import dev.quadstingray.quartz.manager.api.model.JobConfig
import dev.quadstingray.quartz.manager.ui.QuartzManagerUi
import org.quartz.impl.StdSchedulerFactory

object MyApp extends App {
  // Create server instance
  val server = new Server()

  // Register the Web UI routes
  server.registerAfterLoadedRoutes(QuartzManagerUi.uiRoutes)

  // Get the scheduler and start it
  val scheduler = StdSchedulerFactory.getDefaultScheduler
  scheduler.start()

  // Start the HTTP server
  server.startServer()
}
```

### Scheduling Jobs

```scala
import dev.quadstingray.quartz.manager.api.service.{ClassGraphService, JobSchedulerService}
import dev.quadstingray.quartz.manager.api.model.JobConfig
import org.quartz.impl.StdSchedulerFactory

val jobSchedulerService = new JobSchedulerService(
  new ClassGraphService(),
  StdSchedulerFactory.getDefaultScheduler
)

// Schedule a job with cron expression
jobSchedulerService.scheduleJob(
  JobConfig(
    name = "myJob",
    jobClass = "com.example.MyJob",
    group = None,
    cronExpression = "0 0 0 ? * * *" // Daily at midnight
  )
)
```

### Complete Example with Multiple Jobs

```scala
import dev.quadstingray.quartz.manager.api.Server
import dev.quadstingray.quartz.manager.api.model.JobConfig
import dev.quadstingray.quartz.manager.api.service.{ClassGraphService, JobSchedulerService}
import dev.quadstingray.quartz.manager.ui.QuartzManagerUi
import org.quartz.impl.StdSchedulerFactory

object TestServer extends App {
  // Initialize server
  private val server = new Server()
  server.registerAfterLoadedRoutes(QuartzManagerUi.uiRoutes)

  // Create job scheduler service
  val jobSchedulerService = new JobSchedulerService(
    new ClassGraphService(),
    StdSchedulerFactory.getDefaultScheduler
  )

  // Schedule multiple jobs
  (1 to 10).foreach { i =>
    jobSchedulerService.scheduleJob(
      JobConfig(
        name = s"testJob$i",
        jobClass = "dev.quadstingray.quartz.manager.api.jobs.SampleJob",
        group = None,
        cronExpression = "0 0 0 ? * * 2088" // Run in year 2088
      )
    )
  }

  // Start scheduler and server
  StdSchedulerFactory.getDefaultScheduler.start()
  server.startServer()
}
```

## Configuration

Configure the server using `application.conf`:

```hocon
dev.quadstingray.quartz-manager {
  interface = "0.0.0.0"
  port = 8080

  open.api.enabled = true  # Enable Swagger UI
}
```

## API Documentation

When the server is running with OpenAPI enabled, access the Swagger UI at:

```
http://localhost:8080/docs
```

## API Endpoints

The server provides the following endpoint categories:

- **Authentication**: Token-based authentication
- **Jobs**: Create, update, delete, and trigger jobs
- **Scheduler**: Scheduler control and status
- **History**: Job execution history
- **System**: System information and health checks

## Contributing

Issues and pull requests are welcome! Please report any issues on the [GitHub Issue Tracker](https://github.com/QuadStingray/quartz-manager/issues).

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Links

- **GitHub Repository**: https://github.com/QuadStingray/quartz-manager
- **Issue Tracker**: https://github.com/QuadStingray/quartz-manager/issues
- **Maven Central (API)**: https://mvnrepository.com/artifact/dev.quadstingray/quartz-manager-api
- **Maven Central (UI)**: https://mvnrepository.com/artifact/dev.quadstingray/quartz-manager-ui
- **Tapir Documentation**: https://tapir.softwaremill.com/en/latest/
- **Apache Pekko HTTP**: https://pekko.apache.org/docs/pekko-http/current/
