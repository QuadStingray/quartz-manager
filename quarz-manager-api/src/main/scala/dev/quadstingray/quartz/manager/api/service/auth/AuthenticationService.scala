package dev.quadstingray.quartz.manager.api.service.auth
import dev.quadstingray.quartz.manager.api.model.AuthInputBearerWithBasic
import dev.quadstingray.quartz.manager.api.model.UserInformation
import dev.quadstingray.quartz.manager.api.service.ConfigService
import scala.concurrent.Future
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.tapir.auth
import sttp.tapir.endpoint
import sttp.tapir.model.UsernamePassword

trait AuthenticationService {

  lazy val securedEndpointDefinition = {
    if (ConfigService.getBoolean("dev.quadstingray.quarz-manager.auth.active")) {
      endpoint
        .securityIn(
          auth
            .bearer[Option[String]]()
            .and(auth.basic[Option[UsernamePassword]](WWWAuthenticateChallenge.basic("Quartz Login")))
            .mapTo[AuthInputBearerWithBasic]
        )
        .serverSecurityLogic(
          i => Future.successful { authenticate(i) }
        )
    }
    else {
      endpoint
        .serverSecurityLogic(
          _ =>
            Future.successful {
              val r: Either[Unit, UserInformation] = Right(UserInformation(""))
              r
            }
        )
    }
  }

  def authenticate(input: AuthInputBearerWithBasic): Either[Unit, UserInformation] = {
    if (input.basic.isDefined) {
      val basicAuth = input.basic.get
      authenticate(basicAuth)
    }
    else if (input.bearerToken.isDefined) {
      authenticateWithBearer(input.bearerToken.get)
    }
    else {
      Left()
    }
  }

  def authenticate(basicAuth: UsernamePassword): Either[Unit, UserInformation]
  def authenticateWithBearer(bearer: String): Either[Unit, UserInformation]
}
