package com.personal.parking.usecase.ports

import com.personal.parking.entities.Parking
import com.personal.parking.entities.Ticket

interface ParkingRepository {
    fun getById(parkingId: String): Parking?
    fun addCar(parkingId: String, capacity: Int, ticket: Ticket)
}
