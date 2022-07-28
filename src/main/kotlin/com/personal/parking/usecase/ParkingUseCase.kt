package com.personal.parking.usecase

import com.personal.parking.entities.Car
import com.personal.parking.entities.Ticket
import com.personal.parking.usecase.ports.ParkingRepository
import org.springframework.stereotype.Component
import java.util.*
import kotlin.NoSuchElementException

@Component
class ParkingUseCase(private val repository: ParkingRepository) {

    fun park(parkingId: String, carPlate: String, date: Calendar): ParkTicket {
        val parking = repository.getById(parkingId) ?: throw NoSuchElementException(parkingId)
        val ticket = parking.checking(Car(carPlate,), date)
        repository.addCar(parkingId, parking.capacity, ticket)
        return ticket.toParkTicket()
    }

    fun unpark() {}

    data class ParkTicket(val checkin: Calendar, val plate: String)

}

fun Ticket.toParkTicket() = ParkingUseCase.ParkTicket(this.checking, this.car.plate)
