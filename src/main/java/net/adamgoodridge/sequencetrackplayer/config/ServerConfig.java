package net.adamgoodridge.sequencetrackplayer.config;

import org.apache.catalina.*;
import org.apache.catalina.connector.*;
import org.apache.tomcat.util.descriptor.web.*;
import org.springframework.boot.web.embedded.tomcat.*;
import org.springframework.boot.web.servlet.server.*;
import org.springframework.context.annotation.*;

@Configuration
public class ServerConfig {
	
	@Bean
	public ServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				var securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				var collection = new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};
		tomcat.addAdditionalTomcatConnectors(getHttpConnector());
		return tomcat;
	}
	
	private Connector getHttpConnector() {
		var connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		connector.setScheme("http");
		connector.setPort(8080);
		connector.setSecure(false);
		connector.setRedirectPort(8443);
		return connector;
	}
	
}