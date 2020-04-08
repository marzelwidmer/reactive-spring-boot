package ch.keepcalm.demo.http

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
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

//   ____                  _   _           ____  _         _
//  |  _ \ ___  __Í _  ___| |_(_)_   _____/ ___|| |_ _   _| | ___
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
        GET("/greetings/{name}", ::greetMany)
        GET("/greeting/{name}", ::greetOnce)  // http --stream  :8080/greetings/flux
    }

    private fun greetMany(req: ServerRequest) = ok().contentType(MediaType.TEXT_EVENT_STREAM)
        .body(service.greetMay(GreetingRequest(req.pathVariable("name"))))

    private fun greetOnce(req: ServerRequest) = ok()
        .body(service.greetOnce(GreetingRequest(req.pathVariable("name"))))
}

@Service
class GreetingsService {

    fun greet(name: String) = GreetingResponse(message = "Hello $name @ ${Instant.now()}")


    fun greetMay(request: GreetingRequest) =
        Flux.fromStream(
            Stream.generate { greet(request.name) })
            .delayElements(Duration.ofSeconds(1))

    fun greetOnce(request: GreetingRequest) = Mono.just(greet(request.name))

}


data class GreetingRequest(val name: String)
data class GreetingResponse(val message: String)


//   __  ____     ______
//  |  \/  \ \   / / ___|
//  | |\/| |\ \ / / |
//  | |  | | \ V /| |___
//  |_|  |_|  \_/  \____|
//
//@RestController
//class GreetingsRestcontroller(private val greetingsService: GreetingsService) {
//
//    private val logger = LoggerFactory.getLogger(javaClass)
//
//    @GetMapping("/greeting/{name}")
//    fun greet(@PathVariable name: String): Mono<GreetingsResponse> {
//        logger.info("----> $name")
//        return greetingsService.greetOnce(GreetingsRequest(name))
//    }
//}
