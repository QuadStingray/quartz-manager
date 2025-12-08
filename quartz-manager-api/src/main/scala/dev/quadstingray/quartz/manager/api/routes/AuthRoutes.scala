package dev.quadstingray.quartz.manager.api.routes

import dev.quadstingray.quartz.manager.api.json.CirceSchema
import dev.quadstingray.quartz.manager.api.model.auth.TokenResponse
import dev.quadstingray.quartz.manager.api.model.UserInformation
import dev.quadstingray.quartz.manager.api.service.auth.AuthenticationService
import dev.quadstingray.quartz.manager.api.service.ConfigService
import dev.quadstingray.quartz.manager.api.ActorHandler
import io.circe.generic.auto._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import sttp.capabilities
import sttp.capabilities.pekko.PekkoStreams
import sttp.model.Method
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint

class AuthRoutes(authenticationService: AuthenticationService) extends CirceSchema {
  implicit val ex: ExecutionContext = ActorHandler.requestExecutionContext

  private val loginEndpoint = authenticationService.basicEndpointDefinition
    .tag("Auth")
    .in("api" / "auth")
    .in("login")
    .out(jsonBody[TokenResponse])
    .summary("Login")
    .description("Returns the token for the user to access the API")
    .method(Method.GET)
    .name("login")
    .serverLogic {
      u => _ =>
        Future {
          Right {
            val userInformation = u.asInstanceOf[UserInformation]
            val tokenResponse   = authenticationService.generateAndRegisterToken(userInformation)
            tokenResponse
          }
        }
    }

  private val checkTokenEndpoint = authenticationService.bearerEndpointDefinition
    .tag("Auth")
    .in("api" / "auth")
    .in("token")
    .out(jsonBody[TokenResponse])
    .summary("Token")
    .description("Returns the token for the user to access the API")
    .method(Method.GET)
    .name("checkToken")
    .serverLogic {
      user => _ =>
        Future {
          val userInformation = user.asInstanceOf[UserInformation]
          userInformation.bearerToken
            .flatMap(
              t =>
                authenticationService
                  .getTokenOptionFromCache(t)
                  .map(
                    v => Right(v._2)
                  )
            )
            .getOrElse(Left(authenticationService.notAuthorizedError))
        }
    }

  private val extendTokenEndpoint = authenticationService.bearerEndpointDefinition
    .tag("Auth")
    .in("api" / "auth")
    .in("token")
    .out(jsonBody[TokenResponse])
    .summary("Token")
    .description("Returns the token for the user to access the API")
    .method(Method.POST)
    .name("extendToken")
    .serverLogic {
      user => _ =>
        Future {
          Right {
            val userInformation = user.asInstanceOf[UserInformation]
            authenticationService.generateAndRegisterToken(userInformation)
          }
        }
    }

  lazy val endpoints: List[ServerEndpoint[PekkoStreams with capabilities.WebSockets, Future]] = {
    if (ConfigService.getBoolean("dev.quadstingray.quartz-manager.auth.enabled")) {
      List(loginEndpoint, checkTokenEndpoint, extendTokenEndpoint)
    }
    else {
      List.empty
    }
  }
}
