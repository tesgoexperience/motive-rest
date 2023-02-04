package com.motive.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.motive.rest.user.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private final UserRepo repository;

	@Autowired
	public CustomUserDetailsService(UserRepo repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		com.motive.rest.user.User user = this.repository.findByEmail(email);
		return new User(user.getEmail(), user.getPassword(),
				AuthorityUtils.createAuthorityList(user.getRoles()));
	}

}