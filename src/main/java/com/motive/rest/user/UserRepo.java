package com.motive.rest.user;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;


public interface UserRepo extends CrudRepository<User, UUID> {
	Optional<User> findByUsername(String username);
    List<User> findByUsernameContaining(String search);
}
