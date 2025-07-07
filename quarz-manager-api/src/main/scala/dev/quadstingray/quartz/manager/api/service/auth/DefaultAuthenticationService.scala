package dev.quadstingray.quartz.manager.api.service.auth
import dev.quadstingray.quartz.manager.api.model.UserInformation
import dev.quadstingray.quartz.manager.api.service.ConfigService
import sttp.tapir.model.UsernamePassword

class DefaultAuthenticationService extends AuthenticationService {

  private lazy val userName: String = ConfigService.getString("dev.quadstingray.quarz-manager.auth.default.username")
  private lazy val password: String = ConfigService.getString("dev.quadstingray.quarz-manager.auth.default.password")

  def authenticateWithBearer(bearer: String): Either[Unit, UserInformation] = {
    Left()
  }

  def authenticate(basicAuth: UsernamePassword): Either[Unit, UserInformation] = {
    if (basicAuth.username == userName && basicAuth.password.getOrElse("no_pwd") == password) {
      Right(UserInformation(userName))
    }
    else {
      Left()
    }
  }
}
