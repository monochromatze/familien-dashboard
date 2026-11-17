package de.familiendashboard.infrastructure

import de.familiendashboard.application.DatabaseCheck
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/** Adapter für den Port [DatabaseCheck]: fragt die Datenbank mit `SELECT 1`. */
@Component
class JdbcDatabaseCheck(
    private val jdbcTemplate: JdbcTemplate,
) : DatabaseCheck {
    private val logger = LoggerFactory.getLogger(JdbcDatabaseCheck::class.java)

    override fun isReachable(): Boolean =
        try {
            jdbcTemplate.queryForObject("SELECT 1", Int::class.java) == 1
        } catch (e: DataAccessException) {
            logger.warn("Datenbank nicht erreichbar: {}", e.javaClass.simpleName)
            false
        }
}
