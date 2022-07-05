package com.personal.parking.entities

import org.junit.jupiter.api.Test
import java.util.Calendar
import java.text.SimpleDateFormat
import kotlin.test.*

class ParkingTest {
    val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val openFrom = Calendar.getInstance().also { it.setTime(fmt.parse("2022-06-01T09:00:00")) }
    val closedFrom = Calendar.getInstance().also { it.setTime(fmt.parse("2022-06-01T17:00:00")) }
    
    val parking = Parking(1, 10, openFrom, closedFrom)
    
    @Test
    fun `The parking is closed before 9 o'clock`() {
        assertFalse(
                parking.isOpen(Calendar.getInstance().also { it.setTime(fmt.parse("2022-06-01T08:59:59"))}),
                "Should be closed"
            )
    }
    
    @Test
    fun `The parking is open after 9 o'clock`() {
        assertTrue(
                parking.isOpen(Calendar.getInstance().also { it.setTime(fmt.parse("2022-06-01T09:00:01"))}),
                "Should be closed"
            )
    }
}