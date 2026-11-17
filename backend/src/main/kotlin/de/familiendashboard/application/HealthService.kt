package de.familiendashboard.application

import org.springframework.stereotype.Service

data class HealthStatus(
    val status: String,
    val db: String,
) {
    companion object {
        const val OK = "ok"
    }
}

/** Use Case "Gesundheit prüfen". */
@Service
class HealthService(
    private val databaseCheck: DatabaseCheck,
) {
    fun check(): HealthStatus =
        if (databaseCheck.isReachable()) {
            HealthStatus(status = HealthStatus.OK, db = HealthStatus.OK)
        } else {
            HealthStatus(status = "degraded", db = "down")
        }
}
