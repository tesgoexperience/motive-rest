package com.motive.rest.motive;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.motive.rest.user.User;

public interface MotiveRepo extends CrudRepository<Motive, UUID> {
    List<Motive> findByOwner(User owner);

    @Query(value = "SELECT * FROM motive WHERE end < NOW() OR cancelled=TRUE", nativeQuery = true)
    List<Motive> findByFinishedOrCancelled();

    @Query(value = "SELECT * FROM motive WHERE cancelled=FALSE AND end > NOW()", nativeQuery = true)
    List<Motive> findByOngoing();
    @Query(value = "SELECT * FROM motive WHERE cancelled=FALSE AND end > NOW() AND owner_id=?1", nativeQuery = true)
    List<Motive> findByOngoingWithOwner(UUID owner);
}