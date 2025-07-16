package dev.quadstingray.quartz.manager.api.model
import sttp.model.StatusCode

case class Error(statusCode: StatusCode, errorResponse: ErrorResponse)
