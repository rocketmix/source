package com.essec.microservices;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SocketUtils;

@Configuration
public class ServerPortConfiguration {
	
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ServerPortConfiguration.class);
	
	@Value("${server.port-range:null}")
	private String serverPortRange;
	
	@Value("${server.port:-1}")
	private int serverPort;

	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> customContainer() {
		return new WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>() {
			@Override
			public void customize(ConfigurableServletWebServerFactory factory) {
				if (serverPort >= 0) {
					return;
				}
				if (serverPortRange == null) {
					return;
				}
				String[] ports = serverPortRange.split("-");
				if (ports.length != 2) {
					throw new RuntimeException("Unable to initialize HTTP container. Check your server.port-range property that should be like server.port-range=8080-8099");
				}
				try {
					int minPort = Integer.parseInt(ports[0]);
					int maxPort = Integer.parseInt(ports[1]);
					int port = SocketUtils.findAvailableTcpPort(minPort, maxPort);  
					factory.setPort(port);
					logger.info("Container's HTTP port set to : " + port);
				} catch (Throwable t) {
					throw new RuntimeException("Unable to initialize http container. Check your server.port-range property that should be like server.port-range=8080-8999.", t);
				}
			}
		};
	}


}
