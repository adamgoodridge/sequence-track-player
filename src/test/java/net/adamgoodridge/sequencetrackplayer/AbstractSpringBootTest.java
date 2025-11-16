package net.adamgoodridge.sequencetrackplayer;

import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;

import java.io.*;

@SpringBootTest()
public abstract class AbstractSpringBootTest {
    @Mock
    protected File file;
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.config.name", () -> "application-test");
        registry.add("spring.config.location", () -> "classpath:/application-test.properties");
    }
}
