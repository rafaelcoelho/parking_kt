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
        val parking = getParkingFrom(parkingId)
        val ticket = parking.checking(Car(carPlate), date)
        repository.addCar(parkingId, parking.capacity, ticket)
        return ticket.toParkTicket()
    }

    fun unpark(closedTicket: Ticket.TicketClosed, tenant: String): Car {
        val parking = getParkingFrom(tenant)

        val car = closedTicket.ticket.car

        if (!parking.isCarParked(car)) throw NoSuchElementException("The car with plate ${car.plate} is not parked")

        return car.also { parking.checkout(closedTicket) }
    }

    private fun getParkingFrom(parking: String) = repository.getById(parking) ?:
        throw NoSuchElementException("The parking $parking not found")

    fun closeTicketFor(plate: String, parking: String, closeAt: Calendar): Ticket.TicketOpen? {
        val ticket = repository.getById(parking)
            ?.run { this.parkedCars.getOrDefault(plate, null) }

        return ticket?.close(closeAt)
    }

    data class ParkTicket(val checkin: Calendar, val plate: String)

}

fun Ticket.toParkTicket() = ParkingUseCase.ParkTicket(this.checking, this.car.plate)
