package com.example.routing.request

import kotlinx.serialization.Serializable
import com.example.util.UUIDSerializer
import java.util.UUID

@Serializable
data class TripRequest(
    @Serializable(with = UUIDSerializer::class)  // Anotación para UUID
    val driverId: UUID,            // ID del conductor que toma el viaje
    val startLatitude: Double,     // Coordenadas de inicio (latitud)
    val startLongitude: Double,    // Coordenadas de inicio (longitud)
    val endLatitude: Double,       // Coordenadas de destino (latitud)
    val endLongitude: Double,      // Coordenadas de destino (longitud)
    val estimatedDistance: Double, // Distancia estimada en kilómetros
    val estimatedDuration: Double  // Tiempo estimado en minutos
)
