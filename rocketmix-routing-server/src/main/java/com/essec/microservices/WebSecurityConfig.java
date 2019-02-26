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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.RequestMatcher;

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
				Files.write(newFile.toPath(), "guest=password,ROLE_GUEST,enabled".getBytes(Charset.defaultCharset()));
			}
			return new ReloadableUserDetailsManager(resource);
		} catch (Throwable t) {
			logger.error(t.getMessage());
			return new InMemoryUserDetailsManager(User.withUsername("guest").password(ReloadableUserDetailsManager.passwordEncoder().encode("password")).roles("GUEST").build());
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
		http.authorizeRequests().requestMatchers(catalogRequestMatcher()).authenticated().accessDecisionManager(accessDecisionManager());
		http.httpBasic();
    }
    
    @Bean
    public RequestMatcher catalogRequestMatcher() {
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
				System.out.println(service);
				if (service.equalsIgnoreCase("visioaxess")) {
					return true;
				}
				return false;
			}
		};
    }
    
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
				Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
				for (GrantedAuthority authority : authorities) {
					String role = authority.getAuthority();
					System.out.println(role);
				}
				return ACCESS_GRANTED;
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
    	web.ignoring().antMatchers("/", "/index.html", "/favicon.ico", "/**/*.css", "/**/*.js", "/img/**");
    	web.ignoring().antMatchers("/admin", "/admin/**");
    	web.ignoring().antMatchers("/catalog", "/catalog/swagger-ui/index.html");
    	web.securityInterceptor(new FilterSecurityInterceptor() {
    		
    	});
    }
    
    

}