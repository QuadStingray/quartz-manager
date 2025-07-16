package dev.quadstingray.quartz.manager.api.model.auth
import java.util.Date

case class TokenResponse(authToken: String, expiresAt: Date)
