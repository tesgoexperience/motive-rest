package com.motive.rest.motive.status;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.motive.rest.user.User;
import org.springframework.data.jpa.repository.Modifying;

@Transactional
public interface StatusRepo  extends CrudRepository<Status, UUID> {
    @Query(value="SELECT * FROM status where owner_id=?1 AND TIMESTAMPDIFF(HOUR,create_date,CURDATE()) <=24", nativeQuery = true)
    List<Status> findByOwnerAndNotExpired(User owner);
    
    @Modifying 
    @Query(value="DELETE FROM interest where id=?1", nativeQuery = true)
    void deleteInterest(UUID interest);
}
