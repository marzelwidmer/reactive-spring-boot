package ch.keepcalm.websockets

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant
import java.util.stream.Stream


@SpringBootApplication
@EnableHypermediaSupport(type = [EnableHypermediaSupport.HypermediaType.HAL, EnableHypermediaSupport.HypermediaType.HAL_FORMS])
class WebsocketsServiceApplication

fun main(args: Array<String>) {
    runApplication<WebsocketsServiceApplication>(*args)
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

    @Bean
    fun simpleUrlHandlerMapping(webSocketHandler: WebSocketHandler) =
        SimpleUrlHandlerMapping(mapOf("/ws/greetings" to "webSocketHandler"), 10)

    @Bean
    fun webSocketHandler(greetingService: GreetingService): WebSocketHandler = WebSocketHandler { session ->
        session.send(
            session.receive()
                .map { it.payloadAsText }
                .map { GreetingsRequest(it) }
                .flatMap { greetingService.greet(request = it) }
                .map { it.message }
                .map { session.textMessage(it) }
        )
    }

    @Bean
    fun webSocketHandlerAdapter() = WebSocketHandlerAdapter()

}

@Service
class GreetingService {

    fun greetResponse(name: String) = GreetingsResponse(message = "Hello $name @ ${Instant.now()}")

    fun greet(request: GreetingsRequest) =
        Flux.fromStream(Stream.generate {
            greetResponse(request.name)
        }).delayElements(Duration.ofSeconds(1))
}

data class GreetingsRequest(val name: String)
data class GreetingsResponse(val message: String)
