package de.familiendashboard.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class EnvInfo(
    val env: String,
)

// Sagt dem Frontend, auf welcher Stufe es läuft (dev, qa, prod). Das Image ist überall dasselbe,
// nur die Umgebungsvariable APP_ENV unterscheidet die Stufen.
@RestController
@RequestMapping("/api")
class EnvController(
    @Value("\${app.env}") private val env: String,
) {
    @GetMapping("/env")
    fun env(): EnvInfo = EnvInfo(env)
}
