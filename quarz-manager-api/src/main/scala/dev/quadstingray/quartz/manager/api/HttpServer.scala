package dev.quadstingray.quartz.manager.api

import com.typesafe.scalalogging.LazyLogging
import org.apache.pekko.http.scaladsl.model.AttributeKey
import org.apache.pekko.http.scaladsl.server.RequestContext
import sttp.tapir.server.interceptor.RequestInterceptor
import sttp.tapir.server.interceptor.cors.CORSInterceptor
import sttp.tapir.server.pekkohttp.{PekkoHttpServerInterpreter, PekkoHttpServerOptions}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object HttpServer extends LazyLogging {
  implicit val ex: ExecutionContext = ActorHandler.requestExecutionContext
  val requestIdAttributeKey         = AttributeKey[String]("x-request-id")

  private val serverOptions: PekkoHttpServerOptions = {
    val serverOptions = PekkoHttpServerOptions.customiseInterceptors
      .prependInterceptor(RequestInterceptor.transformServerRequest {
        request =>
          val requestContext = request.underlying.asInstanceOf[RequestContext]
          val changedContext = requestContext.withRequest(requestContext.request.addAttribute(requestIdAttributeKey, Random.alphanumeric.take(10).mkString))
          Future.successful(request.withUnderlying(changedContext))
      })
      .corsInterceptor(CORSInterceptor.default[Future])

    serverOptions.options
  }

  val httpServerInterpreter: PekkoHttpServerInterpreter = PekkoHttpServerInterpreter(serverOptions)

}
