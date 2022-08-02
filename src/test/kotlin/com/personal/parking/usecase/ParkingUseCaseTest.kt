package com.personal.parking.usecase

import com.personal.parking.entities.Car
import com.personal.parking.entities.Parking
import com.personal.parking.entities.Ticket
import com.personal.parking.usecase.ports.ParkingRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import java.text.SimpleDateFormat
import java.util.*

internal class ParkingUseCaseTest {
    private val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private val dateSupplier = { dt: String -> Calendar.getInstance().also { it.time = fmt.parse(dt) } }

    private val parking = Parking(5, dateSupplier("2022-06-01T09:00:00"), dateSupplier("2022-06-01T17:00:00"))

    private val repository: ParkingRepository = object : ParkingRepository {
        override fun getById(parkingId: String): Parking? {
            if (parkingId == "123") return parking
            return null
        }

        override fun addCar(parkingId: String, capacity: Int, ticket: Ticket) {
        }
    }
    private val parkUS = ParkingUseCase(repository)

    @Test
    fun `Park a car should park with success`() {
        val ticket = parkUS.park(parkingId = "123", carPlate = "ASD-1234", date = dateSupplier("2022-06-01T12:00:00"))

        Assertions.assertAll(
            Executable { assertEquals("ASD-1234", ticket.plate) },
            Executable { assertEquals("2022-06-01T12:00:00", fmt.format(ticket.checkin.time)) }
        )
    }

    @Test
    fun `Park a car upon not found parking should throws exception`() {
        assertThrows(java.util.NoSuchElementException::class.java) {
            parkUS.park(parkingId = "000", carPlate = "ASD-1234", date = dateSupplier("2022-06-01T12:00:00"))
        }.let { assertEquals("The parking 000 not found", it.message) }
    }

    @Test
    fun `Close the ticket shall return opened ticket with value round to hours`() {
        parkUS.park(parkingId = "123", carPlate = "ASD-1234", date = dateSupplier("2022-06-01T10:00:00"))
        val oTicket : Ticket.TicketOpen? = parkUS.closeTicketFor(plate = "ASD-1234", parking = "123", closeAt = dateSupplier("2022-06-01T13:00:00"))

        assertEquals(30.0, oTicket!!.total)
    }

    @Test
    fun `Close the ticket and pay the bill shall checkout the car from parking`() {
        parkUS.park(parkingId = "123", carPlate = "ASD-1234", date = dateSupplier("2022-06-01T09:01:00"))
        val oTicket : Ticket.TicketOpen = parkUS.closeTicketFor(plate = "ASD-1234", parking = "123", closeAt = dateSupplier("2022-06-01T12:00:00"))!!

        val closedTicket = oTicket.pay()
        val car: Car = parkUS.unpark(closedTicket = closedTicket, tenant = "123")

        assertEquals("ASD-1234", car.plate)
    }

    @Test
    fun `Checkout car from not found parking id shall throws exception`() {
        parkUS.park(parkingId = "123", carPlate = "ASD-1234", date = dateSupplier("2022-06-01T09:01:00"))
        val oTicket : Ticket.TicketOpen = parkUS.closeTicketFor(plate = "ASD-1234", parking = "123", closeAt = dateSupplier("2022-06-01T12:00:00"))!!

        val closedTicket = oTicket.pay()

        assertThrows<NoSuchElementException> { parkUS.unpark(closedTicket = closedTicket, tenant = "404") }
            .run { assertEquals("The parking 404 not found", this.message) }
    }

    @Test
    fun `Checkout car from and try to pay the bill with the not found shall throws exception`() {
        val cFake = Ticket.TicketClosed(Ticket(dateSupplier("2022-06-01T09:01:00"), Car("FAKE")))

        assertThrows<NoSuchElementException> { parkUS.unpark(closedTicket = cFake, tenant = "123") }
            .run { assertEquals("The car with plate FAKE is not parked", this.message) }
    }
}