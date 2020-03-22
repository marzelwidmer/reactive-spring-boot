package ch.keepcalm.fooservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FooServiceApplication

fun main(args: Array<String>) {
	runApplication<FooServiceApplication>(*args)
}
