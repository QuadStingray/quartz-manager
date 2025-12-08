package dev.quadstingray.quartz.manager.api

import com.typesafe.scalalogging.LazyLogging
import org.apache.pekko.http.scaladsl.model.AttributeKey
import org.apache.pekko.http.scaladsl.server.RequestContext
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random
import sttp.model.Method
import sttp.tapir.server.interceptor.cors.CORSConfig
import sttp.tapir.server.interceptor.cors.CORSConfig.AllowedMethods
import sttp.tapir.server.interceptor.cors.CORSInterceptor
import sttp.tapir.server.interceptor.RequestInterceptor
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter
import sttp.tapir.server.pekkohttp.PekkoHttpServerOptions

object HttpServer extends LazyLogging {
  implicit val ex: ExecutionContext                    = ActorHandler.requestExecutionContext
  lazy val requestIdAttributeKey: AttributeKey[String] = AttributeKey[String]("X-REQUEST-ID")

  private val serverOptions: PekkoHttpServerOptions = {
    val corsConfig = CORSConfig.default.copy(allowedMethods =
      AllowedMethods.Some(Set(Method.GET, Method.HEAD, Method.POST, Method.PUT, Method.DELETE, Method.PATCH, Method.OPTIONS))
    )

    val serverOptions = PekkoHttpServerOptions.customiseInterceptors
      .prependInterceptor(RequestInterceptor.transformServerRequest {
        request =>
          val requestContext = request.underlying.asInstanceOf[RequestContext]
          val changedContext = requestContext.withRequest(requestContext.request.addAttribute(requestIdAttributeKey, Random.alphanumeric.take(10).mkString))
          Future.successful(request.withUnderlying(changedContext))
      })
      .corsInterceptor(CORSInterceptor.customOrThrow[Future](corsConfig))

    serverOptions.options
  }

  val defaultHttpServerInterpreter: PekkoHttpServerInterpreter = PekkoHttpServerInterpreter(serverOptions)

}
