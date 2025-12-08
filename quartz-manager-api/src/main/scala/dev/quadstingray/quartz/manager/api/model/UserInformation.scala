package dev.quadstingray.quartz.manager.api.model

case class UserInformation(name: String, password: Option[String], bearerToken: Option[String])
