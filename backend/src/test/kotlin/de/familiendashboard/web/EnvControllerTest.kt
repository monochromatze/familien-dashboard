package de.familiendashboard.web

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(EnvController::class)
@TestPropertySource(properties = ["app.env=qa"])
class EnvControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `liefert den Namen der Stufe aus der Konfiguration`() {
        mockMvc
            .get("/api/env")
            .andExpect {
                status { isOk() }
                content { json("""{"env":"qa"}""", JsonCompareMode.STRICT) }
            }
    }
}
