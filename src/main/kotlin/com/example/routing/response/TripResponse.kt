package com.example.routing.response

import com.example.util.LocalDateTimeSerializer
import com.example.util.UUIDSerializer
import com.example.model.TripStatus
import kotlinx.serialization.Serializable
import java.util.UUID
import java.time.LocalDateTime

@Serializable
data class TripResponse(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,

    @Serializable(with = UUIDSerializer::class)
    val driverId: UUID,

    val startLocation: LocationResponse,
    val endLocation: LocationResponse,
    val status: TripStatus,

    @Serializable(with = LocalDateTimeSerializer::class)  // Serializador para LocalDateTime
    val startTime: LocalDateTime? = null,

    @Serializable(with = LocalDateTimeSerializer::class)  // Serializador para LocalDateTime
    val endTime: LocalDateTime? = null,

    val estimatedDistance: Double,
    val estimatedDuration: Double,
    val actualDistance: Double? = null,
    val actualDuration: Double? = null
)

@Serializable
data class LocationResponse(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null  // Dirección opcional
)
