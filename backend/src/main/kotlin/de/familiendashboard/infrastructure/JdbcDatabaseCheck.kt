package de.familiendashboard.infrastructure

import de.familiendashboard.application.DatabaseCheck
import org.springframework.jdbc.core.JdbcTemplate

/** Stub für TDD: kompiliert, tut aber bewusst das Falsche. Wird im nächsten Commit ersetzt. */
@Suppress("UnusedPrivateProperty")
class JdbcDatabaseCheck(
    private val jdbcTemplate: JdbcTemplate,
) : DatabaseCheck {
    override fun isReachable(): Boolean = true
}
