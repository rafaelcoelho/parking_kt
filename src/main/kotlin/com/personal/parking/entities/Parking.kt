package com.personal.parking.entities

import java.util.Date
import java.util.Calendar
import java.time.LocalDateTime
import com.personal.parking.entities.Car

class Parking (
    val code: Int, 
    val capacity: Int, 
    val openFrom: Calendar, 
    val closedFrom: Calendar) 
    {

    var parkedCars: MutableMap<String, Car> = mutableMapOf()

    fun isOpen(currentTime: Calendar): Boolean {
        return currentTime.time > openFrom.time && currentTime.time < closedFrom.time
    }


    fun park(car: Car) {
        if (parkedCars.containsKey(car.plate)) {
            throw RuntimeException("The car is with plate ${car.plate} is already parked")
        }
        
        parkedCars["car.plate"] = car
    }   

}