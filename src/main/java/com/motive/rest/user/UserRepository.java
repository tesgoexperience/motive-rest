package com.motive.rest.user;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.Repository;

@RepositoryRestResource(exported = false)
public interface UserRepository extends Repository<User, Long> {
	User save(User user);
	User findByEmail(String email);
    Iterable<User> findAll();
	void deleteAll();
}
