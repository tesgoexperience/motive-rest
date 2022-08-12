
package com.motive.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.motive.rest.user.UserRepo;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private final UserRepo repository;

	@Autowired
	public CustomUserDetailsService(UserRepo repository) {
		this.repository = repository;
	}

	public com.motive.rest.user.User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return this.repository.findByEmail(authentication.getName());
	}

	public UserDetails getCurrentUserDetails(){
		com.motive.rest.user.User current = getCurrentUser();
		if (current!=null)
			return loadUserByUsername(current.getEmail());
		return null;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		com.motive.rest.user.User user = this.repository.findByEmail(email);
		return new User(user.getEmail(), user.getPassword(),
				AuthorityUtils.createAuthorityList(user.getRoles()));
	}

}