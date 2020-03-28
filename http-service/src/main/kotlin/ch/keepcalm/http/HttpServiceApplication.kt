package ch.keepcalm.http

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HttpServiceApplication

fun main(args: Array<String>) {
    runApplication<HttpServiceApplication>(*args)
}

