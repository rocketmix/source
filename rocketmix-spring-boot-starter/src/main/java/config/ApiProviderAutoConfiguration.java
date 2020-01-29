package config;

import org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.essec.microservices.JaxRsConfiguration;
import com.essec.microservices.SecurityConfiguration;
import com.essec.microservices.catalog.SwaggerConfiguration;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true)
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableHystrixDashboard
@Import({SecurityConfiguration.class, SwaggerConfiguration.class, CxfAutoConfiguration.class, JaxRsConfiguration.class })
public class ApiProviderAutoConfiguration {



}
