package net.adamgoodridge.sequencetrackplayer.commit;

import net.adamgoodridge.sequencetrackplayer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class CommitTimeControllerTest extends AbstractSpringBootTest {

    private final MockMvc mockMvc;
    @Autowired
	public CommitTimeControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@Test
    void commitTimeEndpointReturnsJson() throws Exception {
        mockMvc.perform(get("/api/commit-time"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.commitTime").value("Test commit time"));
    }
}