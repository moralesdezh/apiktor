package com.example.routing

import com.example.model.User
import com.example.routing.request.UserRequest
import com.example.routing.response.UserResponse
import com.example.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import java.util.UUID

fun Route.userRoute(
    userService: UserService
) {
    // Ruta para crear un nuevo usuario
    post {
        val userRequest = call.receive<UserRequest>()

        // Convertir el request a modelo de User
        val createdUser = userService.save(
            user = userRequest.toModel()
        ) ?: return@post call.respond(HttpStatusCode.BadRequest)

        // Devolver el ID del usuario creado en los headers
        call.response.header(
            name = "id",
            value = createdUser.id.toString()
        )
        call.respond(
            message = HttpStatusCode.Created
        )
    }

    // Rutas protegidas con autenticación
    authenticate {
        // Obtener todos los usuarios
        get {
            val users = userService.findAll()

            call.respond(
                message = users.map(User::toResponse)
            )
        }

        // Obtener un usuario por su ID
        get("/{id}") {
            val idParam = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            // Asegurarse de que el ID es un UUID válido
            val id: UUID = try {
                UUID.fromString(idParam)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest, "Invalid UUID format")
            }

            val foundUser = userService.findById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            // Verificar que el username del usuario coincida con el username del JWT
            if (foundUser.username != extractPrincipalUsername(call)) {
                return@get call.respond(HttpStatusCode.Forbidden)
            }

            // Devolver el usuario encontrado
            call.respond(
                message = foundUser.toResponse()
            )
        }
    }
}

// Función auxiliar para extraer el username del token JWT
fun extractPrincipalUsername(call: ApplicationCall): String? =
    call.principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("username")
        ?.asString()

// Extensión para convertir un UserRequest a un modelo de User
private fun UserRequest.toModel(): User =
    User(
        id = UUID.randomUUID(),       // Generar UUID al crear el usuario
        username = this.username,
        email = this.email,           // Asignar el email desde el request
        passwordHash = this.password, // Asegúrate de que en producción esto sea un hash
        role = this.role,             // Asignar el rol desde el request
        createdAt = java.time.LocalDateTime.now() // Generar fecha actual
    )

// Extensión para convertir un User a UserResponse
private fun User.toResponse(): UserResponse =
    UserResponse(
        id = this.id,
        username = this.username
    )
