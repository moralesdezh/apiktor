package com.example.routing

import com.example.model.Trip
import com.example.model.Location
import com.example.model.TripStatus
import com.example.repository.UserRepository
import com.example.routing.request.TripRequest
import com.example.routing.response.LocationResponse
import com.example.routing.response.TripResponse
import com.example.service.TripService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.tripRoute(
    tripService: TripService,
    userRepository: UserRepository
) {
    route("/trips") {


        // Autenticación solo en los GET si es necesario
        authenticate {
            // Ruta para crear un nuevo viaje
            post {
                val tripRequest = call.receive<TripRequest>()

                val createdTrip = tripService.save(
                    trip = tripRequest.toModel(userRepository)
                ) ?: return@post call.respond(HttpStatusCode.BadRequest)

                call.response.header(
                    name = "id",
                    value = createdTrip.id.toString()
                )
                call.respond(HttpStatusCode.Created)
            }

            get {
                val trips = tripService.findAll()
                call.respond(trips.map { it.toResponse() })
            }

            get("/{id}") {
                val idParam = call.parameters["id"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val id: UUID = try {
                    UUID.fromString(idParam)
                } catch (e: IllegalArgumentException) {
                    return@get call.respond(HttpStatusCode.BadRequest, "Invalid UUID format")
                }

                val foundTrip = tripService.findById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(foundTrip.toResponse())
            }
        }
    }
}


// Extensión para convertir un TripRequest a un modelo de Trip
private fun TripRequest.toModel(userRepository: UserRepository): Trip =
    Trip(
        id = UUID.randomUUID(),       // Generar UUID al crear el viaje
        driver = userRepository.findById(this.driverId)!!,  // Obtener el conductor por ID
        startLocation = Location(
            latitude = this.startLatitude,
            longitude = this.startLongitude
        ),
        endLocation = Location(
            latitude = this.endLatitude,
            longitude = this.endLongitude
        ),
        status = TripStatus.ASSIGNED,  // El viaje inicia como "ASIGNADO"
        estimatedDistance = this.estimatedDistance,
        estimatedDuration = this.estimatedDuration
    )

// Extensión para convertir un Trip a TripResponse
private fun Trip.toResponse(): TripResponse =
    TripResponse(
        id = this.id,
        driverId = this.driver.id,
        startLocation = LocationResponse(
            latitude = this.startLocation.latitude,
            longitude = this.startLocation.longitude,
            address = this.startLocation.address
        ),
        endLocation = LocationResponse(
            latitude = this.endLocation.latitude,
            longitude = this.endLocation.longitude,
            address = this.endLocation.address
        ),
        status = this.status,
        startTime = this.startTime,
        endTime = this.endTime,
        estimatedDistance = this.estimatedDistance,
        estimatedDuration = this.estimatedDuration,
        actualDistance = this.actualDistance,
        actualDuration = this.actualDuration
    )
