package de.familiendashboard.web

import de.familiendashboard.application.DatabaseCheck
import de.familiendashboard.application.HealthService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

class FakeDatabaseCheck : DatabaseCheck {
    var reachable = true

    override fun isReachable(): Boolean = reachable
}

@WebMvcTest(HealthController::class)
@Import(HealthService::class, HealthControllerTest.FakeConfig::class)
class HealthControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val databaseCheck: FakeDatabaseCheck,
) {
    @TestConfiguration
    class FakeConfig {
        @Bean
        fun databaseCheck(): FakeDatabaseCheck = FakeDatabaseCheck()
    }

    @BeforeEach
    fun datenbankIstErreichbar() {
        databaseCheck.reachable = true
    }

    @Test
    fun `liefert ok und db ok, wenn die Datenbank erreichbar ist`() {
        mockMvc
            .get("/api/health")
            .andExpect {
                status { isOk() }
                content { json("""{"status":"ok","db":"ok"}""", JsonCompareMode.STRICT) }
            }
    }

    @Test
    fun `liefert 503 degraded und db down, wenn die Datenbank nicht erreichbar ist`() {
        databaseCheck.reachable = false

        mockMvc
            .get("/api/health")
            .andExpect {
                status { isServiceUnavailable() }
                content { json("""{"status":"degraded","db":"down"}""", JsonCompareMode.STRICT) }
            }
    }
}
