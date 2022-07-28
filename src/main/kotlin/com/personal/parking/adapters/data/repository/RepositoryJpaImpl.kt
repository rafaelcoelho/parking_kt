package com.personal.parking.adapters.data.repository

import com.personal.parking.entities.Car
import com.personal.parking.entities.Parking
import com.personal.parking.entities.Ticket
import com.personal.parking.usecase.ports.ParkingRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType.ALL

@Repository
interface SpringRepositoryImpl : CrudRepository<DbParking, UUID>

@Repository
class RepositoryImpl(val repo: SpringRepositoryImpl) : ParkingRepository {
    override fun getById(parkingId: String): Parking? {
        return repo.findByIdOrNull(UUID.fromString(parkingId))?.toDomain()
    }

    override fun addCar(parkingId: String, capacity: Int, ticket: Ticket) {
        val dbParking = repo.findByIdOrNull(UUID.fromString(parkingId))
            ?: throw NoSuchElementException("Error to update parking $parkingId not found")

        val mappedOut = dbParking.apply {
            this.capacity = capacity
            this.carsPlate.add(
                DbTickets(
                    ticket.car.plate,
                    this,
                    ticket.checking
                )
            )
        }

        repo.save(mappedOut)
    }
}

@Entity
@Table(name = "parking")
class DbParking(
    @Id val id: String,
    var capacity: Int,
    var openFrom: Calendar,
    var closedFrom: Calendar,
    @OneToMany(mappedBy = "parking_fk", cascade = [ALL])
    var carsPlate: MutableList<DbTickets>
)


@Entity
@Table(name = "tickets")
class DbTickets(
    @Id val carPlate: String,
    @ManyToOne
    @JoinColumn(name ="parking_fk", nullable = false)
    val parking_fk: DbParking,
    val checkin: Calendar
)

fun DbParking.toDomain() = Parking(this.capacity, this.openFrom, this.closedFrom)
    .also { parking ->
        this.carsPlate.map { it.toDomain() }
            .forEach { ticket -> parking.parkedCars[ticket.car.plate] = ticket }
    }

fun DbTickets.toDomain() = Ticket(this.checkin, Car(this.carPlate))