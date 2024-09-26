package com.example.routing.request

import kotlinx.serialization.Serializable

@Serializable
class UserRequest (
    val username: String,
    val email: String,     // Campo para el correo electrónico
    val password: String,  // Campo para la contraseña (que se almacenará como hash)
    val role: String       // Campo para el rol ('admin' o 'driver')
)
