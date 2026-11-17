package de.familiendashboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

// Standard-Startaufruf von Spring Boot. Der Spread-Operator kopiert einmalig beim Start, das ist unkritisch.
@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
