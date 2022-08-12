package com.motive.rest.user;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

import org.springframework.data.repository.Repository;

@RepositoryRestResource(exported = false)
public interface UserRepo extends Repository<User, Long> {
	User save(User user);
	User findByEmail(String email);
    Iterable<User> findAll();
	User findByUsername(String username);
    List<User> findByUsernameContaining(String search);
}
