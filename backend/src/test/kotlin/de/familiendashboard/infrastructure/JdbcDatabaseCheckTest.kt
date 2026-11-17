package de.familiendashboard.infrastructure

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource

/** Schneller Test ohne Docker: Es lauscht nichts auf Port 1, die Verbindung scheitert sofort. */
class JdbcDatabaseCheckTest {
    @Test
    fun `meldet nicht erreichbar, wenn keine Verbindung moeglich ist`() {
        val dataSource = DriverManagerDataSource("jdbc:postgresql://localhost:1/gibtsnicht", "nutzer", "geheim")
        val check = JdbcDatabaseCheck(JdbcTemplate(dataSource))

        assertFalse(check.isReachable())
    }
}
