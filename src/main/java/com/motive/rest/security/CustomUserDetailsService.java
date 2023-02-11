package com.motive.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.motive.rest.Auth.AuthRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private final AuthRepo repository;

	@Autowired
	public CustomUserDetailsService(AuthRepo repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

}