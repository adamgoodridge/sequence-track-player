package net.adamgoodridge.sequencetrackplayer;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest()
public abstract class AbstractSpringBootTest {
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.config.name", () -> "application-test");
        registry.add("spring.config.location", () -> "classpath:/application-test.properties");
    }
}
