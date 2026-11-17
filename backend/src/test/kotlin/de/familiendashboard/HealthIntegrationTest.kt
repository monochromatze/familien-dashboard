package de.familiendashboard

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/** Ganze Anwendung gegen ein echtes Postgres 16 (Testcontainers). Braucht Docker. */
@Tag("integration")
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class HealthIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
) {
    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:16-alpine")
    }

    @Test
    fun `liefert ok und db ok gegen ein echtes Postgres`() {
        mockMvc
            .get("/api/health")
            .andExpect {
                status { isOk() }
                content { json("""{"status":"ok","db":"ok"}""", JsonCompareMode.STRICT) }
            }
    }
}
