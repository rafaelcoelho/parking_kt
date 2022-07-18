package com.personal.parking.repository

import com.personal.parking.entities.Parking

interface ParkingRepository {
    fun getById(parkingId: String): Parking?
}
