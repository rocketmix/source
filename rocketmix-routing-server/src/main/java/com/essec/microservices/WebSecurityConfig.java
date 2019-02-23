package com.essec.microservices;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
	@Autowired
	private ResourceLoader resourceLoader;
	
	
	Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
	
	
	@Bean
	public UserDetailsManager userDetailsManager() {
		try {
			InstallScriptParameters installScriptParameters = InstallScriptParameters.getInstance();
			String installPath = installScriptParameters.getInstallPath();
			String serviceName = installScriptParameters.getServiceName();
			String fullpath = "file://" + installPath + File.separator + serviceName + "-users.properties";
			Resource resource = resourceLoader.getResource(fullpath);
			if (!resource.exists()) {
				File newFile = resource.getFile();
				newFile.createNewFile();
				Files.write(newFile.toPath(), "admin=admin,ROLE_ADMIN,enabled".getBytes(Charset.defaultCharset()));
			}
			return new ReloadableUserDetailsManager(resource);
		} catch (Throwable t) {
			logger.error(t.getMessage());
			return new InMemoryUserDetailsManager(User.withUsername("admin").password(ReloadableUserDetailsManager.passwordEncoder().encode("admin")).roles("ADMIN").build());
		}
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return ReloadableUserDetailsManager.passwordEncoder();
	}
	
	
	@Scheduled(fixedDelay = 60000)
	public void refreshUsers() {
		UserDetailsManager userDetailsManager = userDetailsManager();
		if (ReloadableUserDetailsManager.class.isInstance(userDetailsManager)) {
			((ReloadableUserDetailsManager) userDetailsManager).refresh();
			this.logger.info("ACL refreshed");
		}
	}
	
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable();
		http.authorizeRequests().antMatchers("/admin/logout").permitAll();
		http.authorizeRequests().antMatchers("/", "/index.html", "/**/*.css", "/**/*.js", "/img/**").permitAll();
		http.authorizeRequests().antMatchers("/catalog/**").permitAll();
		http.authorizeRequests().anyRequest().permitAll();
		http.httpBasic();
    }
    

}