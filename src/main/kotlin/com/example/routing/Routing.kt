package com.example.routing

import com.example.service.TripService
import com.example.service.UserService
import com.example.repository.UserRepository
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.configureRouting(
    userService: UserService,
    tripService: TripService,
    userRepository: UserRepository  // Aceptamos el tercer parámetro
) {
    routing {
        // Ruta raíz para la prueba
        get("/") {
            call.respondText("API is running")
        }

        route("/api") {
            // Rutas de autenticación
            route("/auth") {
                authRoute(userService)
            }

            // Rutas de usuarios
            route("/user") {
                userRoute(userService)
            }

            // Rutas de viajes
            tripRoute(tripService, userRepository)  // Pasamos userRepository a tripRoute
        }
    }
}
