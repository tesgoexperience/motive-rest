package com.motive.rest.user.Friendship.Circle;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.motive.rest.user.User;
public interface CircleRepo  extends CrudRepository<Circle, Long>{
    Optional<Circle> findByOwnerAndName(User owner, String name);
    List<Circle> findByOwner(User owner);
}