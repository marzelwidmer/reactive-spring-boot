package ch.keepcalm.http

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Instant


@SpringBootApplication
@EnableHypermediaSupport(type = [ EnableHypermediaSupport.HypermediaType.HAL, EnableHypermediaSupport.HypermediaType.HAL_FORMS])
class HttpServiceApplication

fun main(args: Array<String>) {
    runApplication<HttpServiceApplication>(*args)
}

@RestController
class GreetingsRestcontroller(private val greetingsService: GreetingsService) {

    private val logger = LoggerFactory.getLogger(javaClass)


    @GetMapping("/greeting/{name}")
    fun greet(@PathVariable name: String): Mono<GreetingsResponse> {
        logger.info("----> $name")
        return greetingsService.greet(GreetingsRequest(name = name))
    }


}

@Service
class GreetingsService {

    fun greet(request: GreetingsRequest) = Mono.just(GreetingsResponse(message = "Hello ${request.name} @ ${Instant.now()}"))

}


data class GreetingsRequest(val name: String)
data class GreetingsResponse(val message: String)
