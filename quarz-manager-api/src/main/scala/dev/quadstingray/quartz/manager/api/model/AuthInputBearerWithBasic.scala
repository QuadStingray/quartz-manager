package dev.quadstingray.quartz.manager.api.model
import sttp.tapir.model.UsernamePassword

case class AuthInputBearerWithBasic(bearerToken: Option[String], basic: Option[UsernamePassword])
