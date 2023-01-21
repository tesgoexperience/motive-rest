package com.motive.rest.motive;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.motive.rest.user.User;

public interface MotiveRepo extends CrudRepository<Motive, Long>{
    List<Motive> findByOwner(User owner);
    List<Motive> findByOwnerAndFinished(User owner, boolean finished);
    List<Motive> findByFinished(boolean finished);
}