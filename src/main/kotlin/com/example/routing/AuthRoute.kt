package com.example.routing

import com.example.routing.request.LoginRequest
import com.example.routing.request.RefreshTokenRequest
import com.example.routing.response.AuthResponse
import com.example.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoute(userService: UserService) {

    post {
        val loginRequest = call.receive<LoginRequest>()

        val authResponse: AuthResponse? = userService.authenticate(loginRequest)

        authResponse?.let {
            call.respond(authResponse)
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }

    post("/refresh") {
        val request = call.receive<RefreshTokenRequest>()

        val newAccessToken: String? = userService.refreshToken(request.token)

        newAccessToken?.let {
            call.respond(
                RefreshTokenRequest(it)
            )
        } ?: call.respond(
            message = HttpStatusCode.Unauthorized
        )
    }
}