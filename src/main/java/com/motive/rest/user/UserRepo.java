package com.motive.rest.user;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

@RepositoryRestResource(exported = false)
public interface UserRepo extends CrudRepository<User, UUID> {
	User findByUsername(String username);
    List<User> findByUsernameContaining(String search);
}
