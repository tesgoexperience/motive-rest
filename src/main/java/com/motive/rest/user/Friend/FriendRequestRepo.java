package com.motive.rest.user.Friend;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

public interface FriendRequestRepo extends CrudRepository<FriendRequest, Long>{
    Optional<FriendRequest> findById(Long id);
    
}
