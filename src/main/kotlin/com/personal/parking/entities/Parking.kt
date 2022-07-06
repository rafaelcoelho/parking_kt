package com.personal.parking.entities

import java.util.Calendar

class Parking(
    val capacity: Int,
    val openFrom: Calendar,
    val closedFrom: Calendar
)
    {

    var parkedCars: MutableMap<String, Ticket> = mutableMapOf()

    fun isOpen(currentTime: Calendar): Boolean {
        return currentTime.time > openFrom.time && currentTime.time < closedFrom.time
    }


    fun checking(car: Car, checkingDate: Calendar): Ticket {
        if (parkedCars.containsKey(car.plate) || !isOpen(checkingDate)) {
            throw RuntimeException("The car is with plate ${car.plate} is already parked")
        }

        val ticket = Ticket(checkingDate, car)
        parkedCars[car.plate] = ticket
        return ticket
    }

    fun isCarParked(car: Car) = parkedCars.containsKey(car.plate)
    fun checkout(paidBill: Ticket.TicketClosed) {
        parkedCars.remove(paidBill.ticket.car.plate)
    }
}