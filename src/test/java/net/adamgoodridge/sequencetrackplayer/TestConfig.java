package net.adamgoodridge.sequencetrackplayer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
/*
@Configuration
@TestConfiguration
public class TestConfig {


    @Bean
    @Primary
    public WebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.setPort(0); // Use random port
        return tomcat;
    }
}
*/