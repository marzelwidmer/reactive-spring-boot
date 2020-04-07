package ch.keepcalm.demo.greetings

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.web.reactive.function.client.WebClient


@SpringBootApplication
class GreetingsApplication

fun main(args: Array<String>) {
    runApplication<GreetingsApplication>(*args)
}


class Client (private val client: WebClient){

    @EventListener(classes = [ApplicationReadyEvent::class])
    fun ready(): Unit {

    }
}