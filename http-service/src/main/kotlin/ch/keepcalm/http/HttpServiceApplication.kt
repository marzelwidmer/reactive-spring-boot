package ch.keepcalm.http

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.time.Instant

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


    @Bean
    fun route() = router {
        GET("/greeting/{name}", ::greet)
        //      val resourceUrl = "/greeting"
        //        GET("$resourceUrl/{name}") { request ->
        //            ServerResponse.ok().body(service.greet(request = GreetingsRequest(name = request.pathVariable("name"))))
        //        }
        //        GET("$resourceUrl/{name}", { serverRequest ->
        //            val name = serverRequest.pathVariable("name")
        //            val request = GreetingsRequest(name = name)
        //            val response = service.greet(request = request)
        //            ServerResponse.ok().body(response)
        //        })
    }

    private fun greet(req: ServerRequest) = ok().body(service.greet(request = GreetingsRequest(name = req.pathVariable("name"))))
}


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
//        return greetingsService.greet(GreetingsRequest(name = name))
//    }
//
//}

@Service
class GreetingsService {

    fun greet(request: GreetingsRequest) = Mono.just(GreetingsResponse(message = "Hello ${request.name} @ ${Instant.now()}"))

}


data class GreetingsRequest(val name: String)
data class GreetingsResponse(val message: String)
