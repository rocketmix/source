package com.essec.microservices;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
	@Value("classpath:users.properties")
	private Resource users;

	@Bean
	public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
		try {
			Properties properties = new Properties();
			properties.load(users.getInputStream());
			return new InMemoryUserDetailsManager(properties);
		} catch (IOException e) {
			e.printStackTrace();
			return new InMemoryUserDetailsManager(User.withUsername("admin").password(passwordEncoder.encode("soleil")).roles("ADMIN").build());
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new PasswordEncoder() {
			
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return rawPassword.toString().equals(encodedPassword);
			}
			
			@Override
			public String encode(CharSequence rawPassword) {
				return rawPassword.toString();
			}
		};
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