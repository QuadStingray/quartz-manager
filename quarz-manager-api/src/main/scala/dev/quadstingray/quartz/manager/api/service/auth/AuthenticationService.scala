package dev.quadstingray.quartz.manager.api.service.auth
import com.github.blemale.scaffeine.Cache
import com.github.blemale.scaffeine.Scaffeine
import dev.quadstingray.quartz.manager.api.model.auth.AuthInputBearerWithBasic
import dev.quadstingray.quartz.manager.api.model.auth.TokenResponse
import dev.quadstingray.quartz.manager.api.model.Error
import dev.quadstingray.quartz.manager.api.model.ErrorResponse
import dev.quadstingray.quartz.manager.api.model.UserInformation
import dev.quadstingray.quartz.manager.api.service.ConfigService
import io.circe.generic.auto._
import io.circe.syntax._
import java.time.Instant
import org.joda.time.DateTime
import pdi.jwt.JwtCirce
import pdi.jwt.JwtClaim
import scala.concurrent.Future
import scala.jdk.DurationConverters._
import sttp.model.headers.WWWAuthenticateChallenge
import sttp.model.StatusCode
import sttp.tapir.auth
import sttp.tapir.endpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.model.UsernamePassword
import sttp.tapir.statusCode

trait AuthenticationService {
  val name: String
  val version: String

  lazy val notAuthorizedError: Error = Error(StatusCode.Unauthorized, ErrorResponse("user not authorized"))
  protected lazy val cache: Cache[String, (UserInformation, TokenResponse)] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(ConfigService.getDuration("dev.quadstingray.quarz-manager.auth.token.duration").toScala)
      .maximumSize(500)
      .build[String, (UserInformation, TokenResponse)]()

  private val basicAuthInput = auth.basic[Option[UsernamePassword]](WWWAuthenticateChallenge.basic("Quartz Login"))
  private val basicEndpoint  = endpoint.errorOut(statusCode.and(jsonBody[ErrorResponse]).mapTo[Error])

  private lazy val bearerBasicEndpointDefinition = basicEndpoint
    .securityIn(
      auth
        .bearer[Option[String]]()
        .and(basicAuthInput)
        .mapTo[AuthInputBearerWithBasic]
    )
    .serverSecurityLogic(
      i => Future.successful { authenticate(i) }
    )

  lazy val bearerEndpointDefinition = {
    basicEndpoint
      .securityIn(auth.bearer[Option[String]]())
      .serverSecurityLogic(
        i => Future.successful { authenticate(AuthInputBearerWithBasic(i, None)) }
      )
  }

  lazy val basicEndpointDefinition = {
    basicEndpoint
      .securityIn(basicAuthInput)
      .serverSecurityLogic(
        i =>
          Future.successful {
            authenticate(AuthInputBearerWithBasic(None, i))
          }
      )
  }

  lazy val securedEndpointDefinition = {
    if (ConfigService.getBoolean("dev.quadstingray.quarz-manager.auth.enabled")) {
      if (ConfigService.getBoolean("dev.quadstingray.quarz-manager.auth.basic")) {
        bearerBasicEndpointDefinition
      }
      else {
        bearerEndpointDefinition
      }
    }
    else {
      basicEndpoint
        .serverSecurityLogic(
          _ =>
            Future.successful {
              val r: Either[Error, UserInformation] = Right(UserInformation("no_auth", None, None))
              r
            }
        )
    }
  }

  def authenticate(input: AuthInputBearerWithBasic): Either[Error, UserInformation] = {
    if (input.basic.isDefined) {
      val basicAuth = input.basic.get
      authenticate(basicAuth)
    }
    else if (input.bearerToken.isDefined) {
      validateToken(input.bearerToken.get)
    }
    else {
      Left(notAuthorizedError)
    }
  }

  def generateAndRegisterToken(user: UserInformation): TokenResponse = {
    val expirationDate = new DateTime().plusMinutes(30)
    val tokenMap       = Map("name" -> user.name)
    val claim = JwtClaim(
      expiration = Some(expirationDate.toDate.toInstant.getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond),
      issuer = Some(s"$name/$version"),
      content = tokenMap.asJson.toString()
    )
    val token = JwtCirce.encode(claim)
    val tR    = TokenResponse(token, expirationDate.toDate)
    addTokenToCache(tR, user)
    tR
  }

  def validateToken(token: String): Either[Error, UserInformation] = {
    val tokenResponse = getTokenOptionFromCache(token)
    if (tokenResponse.isDefined && tokenResponse.get._2.expiresAt.after(new DateTime().toDate)) {
      Right(tokenResponse.get._1)
    }
    else {
      Left(notAuthorizedError)
    }
  }

  def getTokenOptionFromCache(token: String): Option[(UserInformation, TokenResponse)] = {
    cache.getIfPresent(token)
  }

  protected def addTokenToCache(token: TokenResponse, user: UserInformation): Unit = {
    cache.put(token.authToken, (user.copy(password = None, bearerToken = Some(token.authToken)), token))
  }

  def authenticate(basicAuth: UsernamePassword): Either[Error, UserInformation]

}
