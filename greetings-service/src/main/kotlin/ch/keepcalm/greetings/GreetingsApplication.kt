package ch.keepcalm.greetings

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class GreetingsApplication

fun main(args: Array<String>) {
    runApplication<GreetingsApplication>(*args)
}
