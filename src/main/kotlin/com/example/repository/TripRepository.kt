package com.example.repository

import com.example.model.Location
import com.example.model.TripStatus
import com.example.model.Trip
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object Trips : Table("trips") {
    val id = uuid("id").clientDefault { UUID.randomUUID() }
    val driverId = uuid("driver_id").references(Users.id) // Referencia a la tabla de usuarios (conductor)
    val startLocationLatitude = double("start_location_latitude")
    val startLocationLongitude = double("start_location_longitude")
    val endLocationLatitude = double("end_location_latitude")
    val endLocationLongitude = double("end_location_longitude")
    val status = varchar("status", 50)
    val startTime = datetime("start_time").nullable()
    val endTime = datetime("end_time").nullable()
    val estimatedDistance = double("estimated_distance")
    val estimatedDuration = double("estimated_duration")
    val actualDistance = double("actual_distance").nullable()
    val actualDuration = double("actual_duration").nullable()
    val createdAt = datetime("created_at").clientDefault { java.time.LocalDateTime.now() }
    val updatedAt = datetime("updated_at").nullable()
}

class TripRepository {

    // Obtener todos los viajes
    fun findAll(): List<Trip> = transaction {
        Trips.selectAll().map { rowToTrip(it) }
    }

    // Obtener un viaje por ID
    fun findById(id: UUID): Trip? = transaction {
        Trips.select { Trips.id eq id }
            .map { rowToTrip(it) }
            .singleOrNull()
    }

    // Guardar un nuevo viaje
    fun save(trip: Trip): Boolean = transaction {
        val result = Trips.insert {
            it[id] = trip.id
            it[driverId] = trip.driver.id
            it[startLocationLatitude] = trip.startLocation.latitude
            it[startLocationLongitude] = trip.startLocation.longitude
            it[endLocationLatitude] = trip.endLocation.latitude
            it[endLocationLongitude] = trip.endLocation.longitude
            it[status] = trip.status.name
            it[startTime] = trip.startTime
            it[endTime] = trip.endTime
            it[estimatedDistance] = trip.estimatedDistance
            it[estimatedDuration] = trip.estimatedDuration
            it[actualDistance] = trip.actualDistance
            it[actualDuration] = trip.actualDuration
            it[createdAt] = trip.createdAt
            it[updatedAt] = trip.updatedAt
        }
        result.insertedCount > 0
    }

    // Actualizar un viaje existente
    fun update(trip: Trip): Boolean = transaction {
        val result = Trips.update({ Trips.id eq trip.id }) {
            it[driverId] = trip.driver.id
            it[startLocationLatitude] = trip.startLocation.latitude
            it[startLocationLongitude] = trip.startLocation.longitude
            it[endLocationLatitude] = trip.endLocation.latitude
            it[endLocationLongitude] = trip.endLocation.longitude
            it[status] = trip.status.name
            it[startTime] = trip.startTime
            it[endTime] = trip.endTime
            it[estimatedDistance] = trip.estimatedDistance
            it[estimatedDuration] = trip.estimatedDuration
            it[actualDistance] = trip.actualDistance
            it[actualDuration] = trip.actualDuration
            it[updatedAt] = trip.updatedAt ?: java.time.LocalDateTime.now()
        }
        result > 0
    }

    // Convertir el resultado de la consulta en un objeto `Trip`
    private fun rowToTrip(row: ResultRow): Trip {
        return Trip(
            id = row[Trips.id],
            driver = UserRepository().findById(row[Trips.driverId])!!, // Aquí buscamos el conductor usando su repositorio
            startLocation = Location(
                latitude = row[Trips.startLocationLatitude],
                longitude = row[Trips.startLocationLongitude]
            ),
            endLocation = Location(
                latitude = row[Trips.endLocationLatitude],
                longitude = row[Trips.endLocationLongitude]
            ),
            status = TripStatus.valueOf(row[Trips.status]),
            startTime = row[Trips.startTime],
            endTime = row[Trips.endTime],
            estimatedDistance = row[Trips.estimatedDistance],
            estimatedDuration = row[Trips.estimatedDuration],
            actualDistance = row[Trips.actualDistance],
            actualDuration = row[Trips.actualDuration],
            createdAt = row[Trips.createdAt],
            updatedAt = row[Trips.updatedAt]
        )
    }
}