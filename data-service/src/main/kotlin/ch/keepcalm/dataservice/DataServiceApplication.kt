package ch.keepcalm.dataservice

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.data.annotation.Id
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.*

@SpringBootApplication
class DataServiceApplication

fun main(args: Array<String>) {
    runApplication<DataServiceApplication>(*args)
}

@Component
class SampleDataInitializer(private val reservationRepository: ReservationRepository) {

    private val logger = LoggerFactory.getLogger(javaClass)


    @EventListener(classes = [ApplicationReadyEvent::class])
    fun ready(): Unit {
        val reservations = Flux.just("Madhura", "Josh", "Olga", "Marcin", "Stéphane", "Violetta", "Ria", "Dr. Syer")
                .map { name -> Reseration(name = name) }
                .flatMap { reservation -> reservationRepository.save(reservation) }

        reservationRepository
                .deleteAll()
                .thenMany(reservations)
                .thenMany(reservationRepository.findAll())
                .subscribe {
                    reservation ->
                    logger.info("---> $reservation")
                }
        //  Shortform
//        reservationRepository.deleteAll()
//                .thenMany(listOf("Madhura", "Josh", "Olga", "Marcin", "Stéphane", "Violetta", "Ria", "Dr. Syer").toFlux().map { Reseration(name = it) })
//                .flatMap { reservationRepository.save(it) }
//                .thenMany(reservationRepository.findAll())
//                .subscribe { println(it) }
    }

}

interface ReservationRepository : ReactiveCrudRepository<Reseration, String> {
    // https://docs.mongodb.com/manual/core/tailable-cursors/
//    @Tailable
//    fun findByName(name: String): Flux<Reseration>
}


// TODO: 23.03.20  Used for MongoDB
//@Document
//@TypeAlias("reservation")
//data class Reseration(@Id val id: String = UUID.randomUUID().toString(), val name: String)
data class Reseration(@Id val id: String = UUID.randomUUID().toString(), val name: String)