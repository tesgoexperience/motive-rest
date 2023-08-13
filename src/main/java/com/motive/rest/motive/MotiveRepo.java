package com.motive.rest.motive;
import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.motive.rest.user.User;

public interface MotiveRepo extends CrudRepository<Motive, UUID>{
    List<Motive> findByOwner(User owner);
    List<Motive> findByOwnerAndFinished(User owner, boolean finished);
    List<Motive> findByFinished(boolean finished);
}