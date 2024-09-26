package com.example.model

import java.util.UUID
import java.time.LocalDateTime

data class User(
    val id: UUID = UUID.randomUUID(),  // Generar UUID en el código
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String,                  // El campo `role` puede ser 'admin' o 'driver'
    val createdAt: LocalDateTime = LocalDateTime.now()  // Generar la fecha en el código
)
