package de.familiendashboard.web

import de.familiendashboard.application.HealthService
import de.familiendashboard.application.HealthStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HealthController(
    private val healthService: HealthService,
) {
    @GetMapping("/health")
    fun health(): ResponseEntity<HealthStatus> {
        val result = healthService.check()
        val httpStatus =
            if (result.status == HealthStatus.OK) HttpStatus.OK else HttpStatus.SERVICE_UNAVAILABLE
        return ResponseEntity.status(httpStatus).body(result)
    }
}
