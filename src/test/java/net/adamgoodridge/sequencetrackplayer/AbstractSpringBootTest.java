package net.adamgoodridge.sequencetrackplayer;

import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;

@SpringBootTest
@ActiveProfiles("test")
@Import({AbstractSpringBootTest.TestMockMvcConfig.class})
public abstract class AbstractSpringBootTest {
    @Mock
    protected File file;

    @TestConfiguration
    static class TestMockMvcConfig {
        @Bean
        public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
            return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }
    }
}
