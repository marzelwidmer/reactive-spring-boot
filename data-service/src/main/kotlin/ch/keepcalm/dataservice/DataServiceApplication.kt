package ch.keepcalm.dataservice

//import org.springframework.data.r2dbc.repository.Query
import io.r2dbc.spi.ConnectionFactory
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.context.support.beans
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux

@SpringBootApplication
class DataServiceApplication {

    @Bean
    fun r2dbcTransactionManager(cf: ConnectionFactory): R2dbcTransactionManager {
        return R2dbcTransactionManager(cf)
    }
}

fun main(args: Array<String>) {
    runApplication<DataServiceApplication>(*args) {
        val context = beans {
            bean {
                // R2dbcTransactionManager()
            }
        }
        addInitializers(context)
    }
}

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val transactionalOperator: TransactionalOperator
) {

    fun saveAll(vararg names: String?): Flux<Reservation> {
        val reservations = Flux.fromArray(names)
            .map { name -> Reservation(id = null, name = name) } // TODO: 24.03.20 Take care !!
            .flatMap { reservationRepository.save(it) }
        return transactionalOperator.transactional(reservations)
    }
}

@Component
class SampleDataInitializer(private val reservationRepository: ReservationRepository, private val databaseClient: DatabaseClient,
                            private val reservationService: ReservationService) {

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

        val reservations = reservationService.saveAll("Foo", "Bar")
        // Transaction Service is used...
//        val reservations = Flux.just("Madhura", "Josh", "Olga", "Marcin", "Stéphane", "Violetta", "Ria", "Dr. Syer")
//            .map { name -> Reservation(id = null, name = name) }
//                .flatMapSequential { reservationRepository.save(it) }
//            .flatMap { reservationRepository.save(it) }
//
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
data class Reservation(@Id val id: Int?, @Column("name") val name: String?)
