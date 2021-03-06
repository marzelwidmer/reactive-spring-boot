package ch.keepcalm.demo.websocket

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant
import java.util.stream.Stream

@SpringBootApplication
class WebSocketApplication

fun main(args: Array<String>) {
    runApplication<WebSocketApplication>(*args)
}


//class ExampleHandler(private val greetingService: GreetingWebSocketconfiguration.GreetingService) : WebSocketHandler {
// FROM   override fun handle(session: WebSocketSession): Mono<Void> {
//        val receive = session.receive()
//        val names = receive.map { it.payloadAsText }
//        val requestFlux: Flux<GreetingWebSocketconfiguration.GreetingRequest> = names.map { GreetingWebSocketconfiguration.GreetingRequest(it) }
//        val greetingsResponseFlux = requestFlux.flatMap { greetingService.greet(request = it) }
//        val map : Flux<String> = greetingsResponseFlux.map { GreetingWebSocketconfiguration.GreetingResponse::message.toString() }
//        val webSocketMessageFlux : Flux<WebSocketMessage> = map.map { session.textMessage(it) }
//        return session.send(webSocketMessageFlux)
//    }
// TO
//    override fun handle(session: WebSocketSession): Mono<Void> =
//        session.send(
//            session.receive()
//                .map { it.payloadAsText }
//                .map { GreetingWebSocketconfiguration.GreetingRequest(it) }
//                .flatMap { greetingService.greet(request = it) }
//                .map { GreetingWebSocketconfiguration.GreetingResponse::message.toString() }
//                .map { session.textMessage(it) }
//        )
//}

@Configuration
class GreetingWebSocketConfiguration {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun simpleUrlHandlerMapping(webSocketHandler: WebSocketHandler) =
        SimpleUrlHandlerMapping(mapOf("/ws/greetings" to "webSocketHandler"), 10)

    @Bean
    fun webSocketHandler(greetingService: GreetingService): WebSocketHandler = WebSocketHandler { session ->
        session.send(
            session.receive()
                .map { it.payloadAsText }
                .map { GreetingRequest(it) }
                .flatMap { greetingService.greet(request = it) }
                .map { it.message }
                .map { session.textMessage(it) }
                .doOnEach { log.info("--> $it.type") }
                .doFinally { log.info("--> finally: $it.type") }
        )
    }

    @Bean
    fun webSocketHandlerAdapter() = WebSocketHandlerAdapter()

}

@Service
class GreetingService {

    fun greetResponse(name: String) = GreetingResponse(message = "Hello $name @ ${Instant.now()}")

    fun greet(request: GreetingRequest) =
        Flux.fromStream(Stream.generate {
            greetResponse(request.name)
        }).delayElements(Duration.ofSeconds(1))
}

data class GreetingRequest(val name: String)
data class GreetingResponse(val message: String)
