package com.personal.parking.entities

import java.util.Calendar

open class Ticket(
    val checking: Calendar,
    val car: Car
) {
    private var isPaid : Boolean = false

    fun close(checkoutDate: Calendar) = TicketOpen(this, checkoutDate)

    inner class TicketOpen (
            private val ticket: Ticket,
            private val checkout: Calendar
        ) {
        private val period : Long = checkout.timeInMillis - checking.timeInMillis
        val total : Double = period * 10.0

        fun pay(): TicketClosed {
            ticket.isPaid = true
            return TicketClosed(ticket)
        }
    }

    data class TicketClosed(val ticket: Ticket)
}