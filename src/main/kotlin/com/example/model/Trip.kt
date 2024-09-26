package com.example.model

import java.util.UUID
import java.time.LocalDateTime

data class Trip(
    val id: UUID = UUID.randomUUID(),  // Genera UUID automáticamente
    val driver: User,                  // Referencia completa al objeto `User`
    val startLocation: Location,       // Coordenadas GPS del punto de partida
    val endLocation: Location,         // Coordenadas GPS del destino
    val status: TripStatus,            // Estado del viaje
    val startTime: LocalDateTime? = null,  // Tiempo de inicio del viaje
    val endTime: LocalDateTime? = null,    // Tiempo de finalización
    val estimatedDistance: Double,     // Distancia estimada en kilómetros
    val estimatedDuration: Double,     // Tiempo estimado del viaje en minutos
    val actualDistance: Double? = null,    // Distancia recorrida real (para completados)
    val actualDuration: Double? = null,    // Duración real del viaje
    val createdAt: LocalDateTime = LocalDateTime.now(),  // Fecha y hora en que se creó el viaje
    val updatedAt: LocalDateTime? = null                 // Última actualización del viaje
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null  // Dirección opcional
)

enum class TripStatus {
    ASSIGNED,   // El viaje fue asignado a un conductor pero aún no ha comenzado
    IN_PROGRESS, // El viaje está en curso
    COMPLETED   // El viaje ha sido completado
}
