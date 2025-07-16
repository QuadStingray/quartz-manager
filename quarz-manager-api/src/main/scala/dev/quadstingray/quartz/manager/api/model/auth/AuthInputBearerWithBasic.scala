package dev.quadstingray.quartz.manager.api.model.auth

import sttp.tapir.model.UsernamePassword

case class AuthInputBearerWithBasic(bearerToken: Option[String], basic: Option[UsernamePassword])
