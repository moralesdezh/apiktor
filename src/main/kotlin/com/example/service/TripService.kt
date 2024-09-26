package com.example.service

import com.example.model.Trip
import com.example.repository.TripRepository
import java.util.UUID

class TripService(
    private val tripRepository: TripRepository
) {

    // Encontrar todos los viajes
    fun findAll(): List<Trip> = tripRepository.findAll()

    // Encontrar un viaje por su UUID
    fun findById(id: UUID): Trip? = tripRepository.findById(id)

    // Guardar un nuevo viaje
    fun save(trip: Trip): Trip? {
        // Verificar si el conductor está disponible o si hay restricciones adicionales
        val existingTrip = tripRepository.findById(trip.id)

        return if (existingTrip == null) {
            tripRepository.save(trip)
            trip
        } else null  // Si ya existe un viaje con ese ID, no se guarda
    }

    // Actualizar un viaje
    fun update(trip: Trip): Boolean {
        // Verificar si el viaje existe antes de actualizarlo
        val existingTrip = tripRepository.findById(trip.id)
        return if (existingTrip != null) {
            tripRepository.update(trip)
            true
        } else false
    }
}
