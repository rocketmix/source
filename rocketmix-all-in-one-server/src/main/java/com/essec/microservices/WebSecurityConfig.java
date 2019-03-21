package com.essec.microservices;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableScheduling
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ResourceLoader resourceLoader;

	Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

	@Bean
	public ReloadableUserDetailsManager userDetailsManager() {
		try {
			InstallScriptParameters installScriptParameters = InstallScriptParameters.getInstance();
			String installPath = installScriptParameters.getInstallPath();
			String fullpath = "file://" + installPath + File.separator + "users.properties";
			Resource resource = resourceLoader.getResource(fullpath);
			if (!resource.exists()) {
				File newFile = resource.getFile();
				newFile.createNewFile();
				Files.write(newFile.toPath(), "admin=admin,ROLE_ADMIN,enabled\nguest=password,ROLE_GUEST,enabled".getBytes(Charset.defaultCharset()));
				logger.info("New ACL file created at : " + newFile.toPath());
			}
			return new ReloadableUserDetailsManager(resource);
		} catch (Throwable t) {
			UserDetails guestUser = User.withUsername("guest").password(ReloadableUserDetailsManager.passwordEncoder().encode("password")).roles("GUEST").build();
			UserDetails demoUser = User.withUsername("demo").password(ReloadableUserDetailsManager.passwordEncoder().encode("demo")).roles("DEMO").build();
			InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager(guestUser, demoUser);
			return new ReloadableUserDetailsManager(inMemoryUserDetailsManager);
		}
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return ReloadableUserDetailsManager.passwordEncoder();
	}

	@Scheduled(initialDelay = 0, fixedDelay = 60000)
	public void refreshUsers() {
		UserDetailsManager userDetailsManager = userDetailsManager();
		if (ReloadableUserDetailsManager.class.isInstance(userDetailsManager)) {
			((ReloadableUserDetailsManager) userDetailsManager).refresh();
		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		// Secure access to services catalog
		http.authorizeRequests().requestMatchers(catalogRequestMatcher()).authenticated().accessDecisionManager(accessDecisionManager());
		http.httpBasic();
	}

	/**
	 * Secure access to services catalog. <br/>
	 * If a user is declared in users.properties (such as
	 * demo=password,ROLE_DEMO,enabled), <br/>
	 * and his role equals a service name (in uppercase and prefixed by ROLE_),
	 * access this specific catalog <br/>
	 * is automatically secured.<br/>
	 * 
	 * If there's any user with a role that matches a service name, this service
	 * is public (not authentication required) <br/>
	 * 
	 * @return r
	 */
	@Bean
	public RequestMatcher catalogRequestMatcher() {
		ReloadableUserDetailsManager reloadableUserDetailsManager = userDetailsManager();
		return new RequestMatcher() {
			@Override
			public boolean matches(HttpServletRequest request) {
				String requestURI = request.getRequestURI();
				if (!requestURI.equalsIgnoreCase("/catalog/swagger-docs/proxy")) {
					return false;
				}
				String service = request.getParameter("vipaddress");
				if (StringUtils.isBlank(service)) {
					return false;
				}
				service = "ROLE_" + service.toUpperCase();
				if (reloadableUserDetailsManager.roleExists(service)) {
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * Confirm if a user has access to a services catalog <br/>
	 * Filtering is based on custom Swagger proxy loader url <br/>
	 * 
	 * @return a
	 */
	@Bean
	public AccessDecisionVoter<FilterInvocation> propertiesBasedVoter() {
		return new AccessDecisionVoter<FilterInvocation>() {
			@Override
			public boolean supports(ConfigAttribute attribute) {
				return true;
			}

			@Override
			public boolean supports(Class<?> clazz) {
				return clazz.isAssignableFrom(FilterInvocation.class);
			}

			@Override
			public int vote(Authentication authentication, FilterInvocation object, Collection<ConfigAttribute> attributes) {
				HttpServletRequest request = object.getHttpRequest();
				String requestURI = request.getRequestURI();
				if (!requestURI.equalsIgnoreCase("/catalog/swagger-docs/proxy")) {
					return ACCESS_ABSTAIN;
				}
				String service = request.getParameter("vipaddress");
				if (StringUtils.isBlank(service)) {
					return ACCESS_ABSTAIN;
				}
				service = "ROLE_" + service.toUpperCase();
				Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
				for (GrantedAuthority authority : authorities) {
					String role = authority.getAuthority();
					if (service.equals(role)) {
						return ACCESS_GRANTED;
					}
				}
				return ACCESS_DENIED;
			}
		};
	}


	@Bean
	public AccessDecisionManager accessDecisionManager() {
		List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
		decisionVoters.add(new WebExpressionVoter());
		decisionVoters.add(new RoleVoter());
		decisionVoters.add(new AuthenticatedVoter());
		decisionVoters.add(propertiesBasedVoter());
		return new UnanimousBased(decisionVoters);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/", "/index.html", "/favicon.ico", "/**/*.css", "/**/*.js", "/img/**"); // Let access to portal web resources
		web.ignoring().antMatchers("/admin", "/admin/**"); // Let access to management server (authentication is delegated to authentication server 
		web.ignoring().antMatchers("/catalog", "/catalog/swagger-ui/index.html");  // Let access to Swagger HTML resources
		web.ignoring().antMatchers("/openapi/default.json"); // Let access to defauit api definition
	}
	

}