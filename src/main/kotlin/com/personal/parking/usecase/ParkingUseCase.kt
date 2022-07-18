package com.personal.parking.usecase

import com.personal.parking.entities.Car
import com.personal.parking.entities.Ticket
import com.personal.parking.repository.ParkingRepository
import java.util.*
import kotlin.NoSuchElementException

class ParkingUseCase(private val repository: ParkingRepository) {

    fun park(parkingId: String, carPlate: String, date: Calendar): ParkTicket {
        val parking = repository.getById(parkingId) ?: throw NoSuchElementException(parkingId)
        return parking.checking(Car(carPlate), date)
            .toParkTicket()
    }

    fun unpark() {}

    data class ParkTicket(val checkin: Calendar, val plate: String) {
    }

}

fun Ticket.toParkTicket() = ParkingUseCase.ParkTicket(this.checking, this.car.plate)
