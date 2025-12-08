package dev.quadstingray.quartz.manager.api.service.auth
import dev.quadstingray.quartz.manager.api.model.Error
import dev.quadstingray.quartz.manager.api.model.ErrorResponse
import dev.quadstingray.quartz.manager.api.model.UserInformation
import dev.quadstingray.quartz.manager.api.service.ConfigService
import scala.jdk.CollectionConverters._
import sttp.model.StatusCode
import sttp.tapir.model.UsernamePassword

case class DefaultAuthenticationService(name: String, version: String) extends AuthenticationService {

  private lazy val userList: List[UserInformation] = ConfigService
    .getStringList("dev.quadstingray.quartz-manager.auth.users")
    .asScala
    .map(
      s => {
        val parts = s.split(":")
        UserInformation(parts.head, Some(parts.last), None)
      }
    )
    .toList

  def authenticate(basicAuth: UsernamePassword): Either[Error, UserInformation] = {
    val userOption = userList.find(_.name.equals(basicAuth.username))
    if (userOption.isEmpty) {
      Left(notAuthorizedError)
    }
    else {
      if (userOption.get.password.isDefined && basicAuth.password.equals(userOption.get.password)) {
        Right(userOption.get)
      }
      else {
        Left(notAuthorizedError)
      }
    }
  }

}
