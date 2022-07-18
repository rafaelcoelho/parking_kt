package com.personal.parking.entities

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.text.SimpleDateFormat
import kotlin.test.*

internal class ParkingTest {
    private val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    private val openFrom: Calendar = Calendar.getInstance().also { it.time = fmt.parse("2022-06-01T09:00:00") }
    private val closedFrom: Calendar = Calendar.getInstance().also { it.time = fmt.parse("2022-06-01T17:00:00") }
    private val dateSupplier = { dt: String -> Calendar.getInstance().also { it.time = fmt.parse(dt) } }

    val parking = Parking(10, openFrom, closedFrom)

    @Test
    fun `The parking is closed before 9 o'clock`() {
        assertFalse(
            parking.isOpen(Calendar.getInstance().also { it.setTime(fmt.parse("2022-06-01T08:59:59")) }),
            "Should be closed"
        )
    }

    @Test
    fun `The parking is open after 9 o'clock`() {
        assertTrue(
            parking.isOpen(Calendar.getInstance().also { it.setTime(fmt.parse("2022-06-01T09:00:01")) }),
            "Should be open"
        )
    }

    @Test
    fun `The parking is closed after 17 o'clock`() {
        assertFalse(
            parking.isOpen(Calendar.getInstance().also { it.setTime(fmt.parse("2022-06-01T17:00:01")) }),
            "Should be closed"
        )
    }

    @Test
    fun `Parking a car should park`() {
        val car = Car("ABC-1234")

        parking.checking(car, dateSupplier("2022-06-01T15:00:01"))

        assertTrue(
            parking.isCarParked(car),
            "The car should be parked"
        )
    }

    @Test
    fun `Emit the bill should return ticket`() {
        val car = Car("ABC-9988")
        val ticket = parking.checking(car, dateSupplier("2022-06-01T12:00:00"))

        val bill = ticket.close(dateSupplier("2022-06-01T15:00:00"))
        val paidBill = bill.pay()

        parking.checkout(paidBill)

        assertFalse(
            parking.isCarParked(car),
            "The car should not be in the parking"
        )
    }

    @Test
    fun `Parking full should throws exception to park`() {
        val fullParking = Parking(0, openFrom, closedFrom)

        val exception = Assertions.assertThrows(
            java.lang.RuntimeException::class.java
        ) { fullParking.checking(Car("ABC-9988"), dateSupplier("2022-06-01T12:00:00")) }

        Assertions.assertEquals("The parking is full", exception.message)
    }

    @Test
    fun `Park a car get full parking if try to park a new car should throws exception to park`() {
        val fullParking = Parking(1, openFrom, closedFrom)
        fullParking.checking(Car("ABC-9988"), dateSupplier("2022-06-01T12:00:00"))

        val exception = Assertions.assertThrows(
            java.lang.RuntimeException::class.java
        ) { fullParking.checking(Car("ABC-0088"), dateSupplier("2022-06-01T15:00:00")) }

        Assertions.assertEquals("The parking is full", exception.message)
    }

    @Test
    fun `Unpark a car from full parking and park a new car should success`() {
        val fullParking = Parking(1, openFrom, closedFrom)
        fullParking.checking(Car("ABC-9988"), dateSupplier("2022-06-01T12:00:00")).let { it ->
            it.close(dateSupplier("2022-06-01T15:00:00"))
                .let { fullParking.checkout(it.pay()) }
        }

        Assertions.assertDoesNotThrow { fullParking.checking(Car("ABC-0088"), dateSupplier("2022-06-01T15:00:00")) }
    }
}