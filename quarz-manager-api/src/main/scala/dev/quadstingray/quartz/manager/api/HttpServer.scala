package dev.quadstingray.quartz.manager.api

import com.typesafe.scalalogging.LazyLogging
import org.apache.pekko.http.scaladsl.model.AttributeKey
import org.apache.pekko.http.scaladsl.server.RequestContext
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random
import sttp.tapir.server.interceptor.cors.CORSInterceptor
import sttp.tapir.server.interceptor.RequestInterceptor
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter
import sttp.tapir.server.pekkohttp.PekkoHttpServerOptions

object HttpServer extends LazyLogging {
  implicit val ex: ExecutionContext                    = ActorHandler.requestExecutionContext
  lazy val requestIdAttributeKey: AttributeKey[String] = AttributeKey[String]("X-REQUEST-ID")

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

  val defaultHttpServerInterpreter: PekkoHttpServerInterpreter = PekkoHttpServerInterpreter(serverOptions)

}
