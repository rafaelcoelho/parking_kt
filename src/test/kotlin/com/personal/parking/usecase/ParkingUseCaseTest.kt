package com.personal.parking.usecase

import com.personal.parking.entities.Parking
import com.personal.parking.entities.Ticket
import com.personal.parking.usecase.ports.ParkingRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
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
            Executable { Assertions.assertEquals("ASD-1234", ticket.plate) },
            Executable { Assertions.assertEquals("2022-06-01T12:00:00", fmt.format(ticket.checkin.time)) }
        )
    }

    @Test
    fun `Park a car upon not found parking should throws exception`() {
        assertThrows(java.util.NoSuchElementException::class.java) {
            parkUS.park(parkingId = "000", carPlate = "ASD-1234", date = dateSupplier("2022-06-01T12:00:00"))
        }.let { Assertions.assertEquals("000", it.message) }
    }
}