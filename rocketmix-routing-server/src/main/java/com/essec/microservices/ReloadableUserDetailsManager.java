package com.essec.microservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

public class ReloadableUserDetailsManager implements UserDetailsManager {

	private InMemoryUserDetailsManager wrappedUserDetailsManager = new InMemoryUserDetailsManager();

	private Resource usersResource;
	
	private List<String> currentRoles = new ArrayList<>(); 
	
	private Logger logger = LoggerFactory.getLogger(ReloadableUserDetailsManager.class);
	
	
	public ReloadableUserDetailsManager(Resource usersResource) {
		this.usersResource = usersResource;
	}
	
	public ReloadableUserDetailsManager(InMemoryUserDetailsManager userDetailsManager) {
		this.wrappedUserDetailsManager = userDetailsManager;
	}
	
	
	public void refresh() {
		try {
			if (this.usersResource == null) {
				logger.info("ACL not refreshed because it is static");
				return;
			}
			Properties properties = new Properties();
			properties.load(this.usersResource.getInputStream());
			List<String> newRoles = new ArrayList<>(); 
			this.wrappedUserDetailsManager = new InMemoryUserDetailsManager(properties) {
				@Override
				public void createUser(UserDetails user) {
					Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
					for (GrantedAuthority anAuthority : authorities) {
						String role = anAuthority.getAuthority();
						newRoles.add(role);
						logger.info("New role detected : " + role);
					}
					super.createUser(user);
				}
			};
			currentRoles.clear();
			currentRoles.addAll(newRoles);
			logger.info("ACL refreshed from : " + this.usersResource.getURI().getPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean roleExists(String role) {
		return this.currentRoles.contains(role);
	}
	
	public static PasswordEncoder passwordEncoder() {
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
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.wrappedUserDetailsManager.loadUserByUsername(username);
	}

	@Override
	public void createUser(UserDetails user) {
		throw new RuntimeException("Feature not supported because automatic reloading should alter user details");
	}

	@Override
	public void updateUser(UserDetails user) {
		throw new RuntimeException("Feature not supported because automatic reloading should alter user details");
	}

	@Override
	public void deleteUser(String username) {
		throw new RuntimeException("Feature not supported because automatic reloading should alter user details");
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		throw new RuntimeException("Feature not supported because automatic reloading should alter user details");
	}

	@Override
	public boolean userExists(String username) {
		return this.wrappedUserDetailsManager.userExists(username);
	}

	
	
	
}
