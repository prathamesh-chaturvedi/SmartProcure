package com.smartprocure.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smartprocure.entities.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetailsImpl implements UserDetails{
	

	private static final long serialVersionUID = 1L;
	private final User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return List.of(new SimpleGrantedAuthority(user.getUserRole().name()));
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}
	
	public User getUser() {
		return user;
	}

}
