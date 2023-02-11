package com.motive.rest.Auth;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface AuthRepo extends CrudRepository<AuthDetails,UUID>{
    Optional<AuthDetails> findByEmail(String email);
}
