package com.example

import com.example.plugins.*
import com.example.repository.RefreshTokenRepository
import com.example.repository.TripRepository  // Asegúrate de importar TripRepository
import com.example.repository.UserRepository
import com.example.routing.configureRouting
import com.example.service.TripService
import com.example.service.UserService
import com.example.service.JwtService
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Inicializa la conexión a la base de datos
    Databases.init()

    // Repositorios y servicios
    val userRepository = UserRepository()
    val tripRepository = TripRepository()  // Instancia de TripRepository
    val jwtService = JwtService(this, userRepository)
    val refreshTokenRepository = RefreshTokenRepository()
    val userService = UserService(userRepository, jwtService, refreshTokenRepository)

    // Instancia de TripService con TripRepository
    val tripService = TripService(tripRepository)  // Pasamos TripRepository a TripService

    // Configuraciones de Ktor
    configureSerialization()
    configureSecurity(jwtService)
    configureRouting(userService, tripService, userRepository)  // Ahora pasamos userRepository también
}
