package ch.keepcalm.dataservice

//import org.springframework.data.r2dbc.repository.Query
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@SpringBootApplication
class DataServiceApplication

fun main(args: Array<String>) {
    runApplication<DataServiceApplication>(*args)
}

@Component
class SampleDataInitializer(
    private val reservationRepository: ReservationRepository,
    private val databaseClient: DatabaseClient
) {

    private val logger = LoggerFactory.getLogger(javaClass)


    @EventListener(classes = [ApplicationReadyEvent::class])
    fun ready() {

        // LowLevel Database Client
        // Read Database Entries
        this.databaseClient
            .select().from("reservation").`as`(Reservation::class.java)
            .fetch()
            .all()
            .doOnComplete { logger.info("------------------------------------") }
            .subscribe { logger.info("<-- $it") }

        val reservations = Flux.just("Madhura", "Josh", "Olga", "Marcin", "Stéphane", "Violetta", "Ria", "Dr. Syer")
            .map { name -> Reservation(id = null, name = name) }
            .flatMap { reservationRepository.save(it) }
//                .flatMapSequential { reservationRepository.save(it) }

        reservationRepository
            .deleteAll()
            .thenMany(reservations)
            .thenMany(reservationRepository.findAll())
            .subscribe { reservation -> logger.info("---> $reservation") }

        //  Shortform
//        reservationRepository.deleteAll()
//                .thenMany(listOf("Madhura", "Josh", "Olga", "Marcin", "Stéphane", "Violetta", "Ria", "Dr. Syer").toFlux().map { Reseration(name = it) })
//                .flatMap { reservationRepository.save(it) }
//                .thenMany(reservationRepository.findAll())
//                .subscribe { println(it) }
    }

}

// MongoDB
//interface ReservationRepository : ReactiveCrudRepository<Reservation, String> {
//    fun findByName(name: String): Flux<Reservation>
//}
//@Document
//@TypeAlias("reservation")
//data class Reservation(@Id val id: String = UUID.randomUUID().toString(), val name: String)


// Postgres
interface ReservationRepository : ReactiveCrudRepository<Reservation, Int> {
    @Query(value = "select * from reservation where name = $1 ")
    fun findByName(name: String): Flux<Reservation>
}

@Table("Reservation")
data class Reservation(@Id val id: Int?, @Column("name") val name: String)
