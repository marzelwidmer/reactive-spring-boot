package ch.keepcalm.demo.greetings

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker
import org.springframework.context.event.EventListener
import org.springframework.context.support.beans
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@SpringBootApplication
class GreetingsApplication

fun main(args: Array<String>) {
    runApplication<GreetingsApplication>(*args) {

        addInitializers(
            beans {
                bean {
                    WebClient.builder()
                        .baseUrl("http://localhost:8080")
                        .build()
                }
                bean {
                    ReactiveResilience4JCircuitBreakerFactory()
                        .create("greeting")
                }
            }
        )
    }
}

@Component
class Client(private val webClient: WebClient, private val circuitBreaker: ReactiveCircuitBreaker) {

    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(classes = [ApplicationReadyEvent::class])
    fun ready(): Unit {
        val name = "SpringFans"

        webClient
            .get()
            .uri("/greeting/{name}", name)
            .retrieve()
            .bodyToMono(GreetingResponse::class.java)
            .map(GreetingResponse::message)
            .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)))
            .onErrorMap { IllegalArgumentException("The original exception was ${it.localizedMessage}") }
            .onErrorResume {
                when (it) {
                    is IllegalArgumentException -> Mono.just(it.message.toString())
                    else -> Mono.just("Ooopss !!! ${it.message} ")
                }
            }
            .subscribe { log.info("--> Mono: $it") }



        // Call with CircuitBreaker
        circuitBreaker.run(
            webClient
                .get()
                .uri("/greeting/{name}", name)
                .retrieve()
                .bodyToMono(GreetingResponse::class.java)
                .map(GreetingResponse::message)
        ) {
            Mono.just("Ooopss CircuitBreaker !!! ${it.message} ")
        }
            .subscribe { log.info("--> CircuitBreaker Mono: $it") }

    }

}


data class GreetingRequest(val name: String)
data class GreetingResponse(val message: String)

