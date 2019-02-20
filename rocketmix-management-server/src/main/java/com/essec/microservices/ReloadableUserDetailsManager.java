package com.essec.microservices;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

public class ReloadableUserDetailsManager implements UserDetailsManager {

	private InMemoryUserDetailsManager wrappedUserDetailsManager = new InMemoryUserDetailsManager();

	private Resource usersResource;
	
	public ReloadableUserDetailsManager(Resource usersResource) {
		this.usersResource = usersResource;
	}
	
	public void refresh() {
		try {
			Properties properties = new Properties();
			properties.load(this.usersResource.getInputStream());
			this.wrappedUserDetailsManager = new InMemoryUserDetailsManager(properties);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
		this.wrappedUserDetailsManager.createUser(user);
	}

	@Override
	public void updateUser(UserDetails user) {
		this.wrappedUserDetailsManager.updateUser(user);
	}

	@Override
	public void deleteUser(String username) {
		this.wrappedUserDetailsManager.deleteUser(username);
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		this.wrappedUserDetailsManager.changePassword(oldPassword, newPassword);
	}

	@Override
	public boolean userExists(String username) {
		return this.wrappedUserDetailsManager.userExists(username);
	}

	
	
	
}
