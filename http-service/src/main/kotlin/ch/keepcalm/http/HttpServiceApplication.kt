package ch.keepcalm.http

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant
import java.util.stream.Stream

@SpringBootApplication
@EnableHypermediaSupport(type = [EnableHypermediaSupport.HypermediaType.HAL, EnableHypermediaSupport.HypermediaType.HAL_FORMS])
class HttpServiceApplication

fun main(args: Array<String>) {
    runApplication<HttpServiceApplication>(*args)
}

//   ____                 _   _           ____  _         _
//  |  _ \ ___  __ _  ___| |_(_)_   _____/ ___|| |_ _   _| | ___
//  | |_) / _ \/ _` |/ __| __| \ \ / / _ \___ \| __| | | | |/ _ \
//  |  _ <  __/ (_| | (__| |_| |\ V /  __/___) | |_| |_| | |  __/
//  |_| \_\___|\__,_|\___|\__|_| \_/ \___|____/ \__|\__, |_|\___|
//                                                  |___/

// Functional Controller
@Configuration
class RouterConfig(private val service: GreetingsService) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun route() = router {
        GET("/greeting/{name}", ::greet)
        GET("/greetings/{name}", ::greeMany)  // Server-Sent Events
    }

    private fun greet(req: ServerRequest) = ok().body(service.greet(req.pathVariable("name")))
    private fun greeMany(req: ServerRequest) = ok().contentType(MediaType.TEXT_EVENT_STREAM)
        .body(service.greetMay(GreetingsRequest(req.pathVariable("name"))))
}

@Service
class GreetingsService {

    fun greetMay(request: GreetingsRequest) =
        Flux.fromStream(Stream.generate({ greet(request.name) }))
            .delayElements(Duration.ofSeconds(1))

    fun greetOnce(request: GreetingsRequest) = greet(request.name)

    fun greet(name: String) = Mono.just(GreetingsResponse(message = "Hello ${name} @ ${Instant.now()}"))

}


data class GreetingsRequest(val name: String)
data class GreetingsResponse(val message: String)


//   __  ____     ______
//  |  \/  \ \   / / ___|
//  | |\/| |\ \ / / |
//  | |  | | \ V /| |___
//  |_|  |_|  \_/  \____|
//
@RestController
class GreetingsRestcontroller(private val greetingsService: GreetingsService) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/greeting/{name}")
    fun greet(@PathVariable name: String): Mono<GreetingsResponse> {
        logger.info("----> $name")
        return greetingsService.greet(name)
    }

}
