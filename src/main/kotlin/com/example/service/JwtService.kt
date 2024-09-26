package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.repository.UserRepository
import com.example.routing.request.LoginRequest
import io.ktor.http.cio.Request
import io.ktor.server.application.Application
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date

class JwtService(
    private val application: Application,
    private val userRepository: UserRepository
) {

    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private  val audience = getConfigProperty("jwt.audience")

    val realm = getConfigProperty("jwt.realm")

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    fun createAccessToken(username: String) : String =
        createJwtToken(username, 3_600_000)

    fun createRefreshToken(username: String): String =
        createJwtToken(username, 86_400_000)

    private fun createJwtToken(username: String, expireIn: Int): String =
        JWT
            .create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
            .sign(Algorithm.HMAC256(secret))


    fun  customValidator(credential: JWTCredential): JWTPrincipal? {
        val username = extractsername(credential)
        val foundUser = username?.let(userRepository::findByUsername)

        return foundUser?.let {
            if(audienceMatches(credential)) {
                JWTPrincipal(credential.payload)
            } else null
        }
    }

    fun audienceMatches(audience: String): Boolean =
        this.audience == audience

    private fun audienceMatches(credential: JWTCredential): Boolean =
        credential.payload.audience.contains(audience)


    private fun extractsername(credential: JWTCredential): String? =
        credential.payload.getClaim("username").asString()


    private fun getConfigProperty(path: String) =
        application.environment.config.property(path).getString()

}