package ch.keepcalm.demo.greetings

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration


@SpringBootApplication
class GreetingsApplication {
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl("http://localhost:8080")
            //.filter(ExchangeFilterFunctions.basicAuthentication())Å
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<GreetingsApplication>(*args)
}

@Component
class Client(private val webClient: WebClient) {

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
            .retryWhen(Retry.backoff(10, Duration.ofSeconds(10)))
            .onErrorMap { IllegalArgumentException("The original exception was ${it.localizedMessage}") }
            .onErrorResume {
                when (it) {
                    is IllegalArgumentException -> Mono.just(it.message.toString())
                    else -> Mono.just("Ooopss !!! ")
                }
            }
            .subscribe { log.info("--> Mono: $it") }

        webClient
            .get()
            .uri("/greetings/{name}", name)
            .retrieve()
            .bodyToFlux(GreetingResponse::class.java)
            .subscribe { log.info("--> Flux: ${it.message}") }
    }
}


data class GreetingRequest(val name: String)
data class GreetingResponse(val message: String)