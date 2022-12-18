package com.motive.rest.user.friendship;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FriendRepo extends CrudRepository<Friendship, Long>{
    @Query(value="SELECT * FROM friendship WHERE approved=true AND (receiver_id=?1 OR sender_id=?1)", nativeQuery = true)
    List<Friendship> findApprovedRequests(Long id);

    @Query(value="SELECT * FROM friendship WHERE approved=?2 AND sender_id=?1", nativeQuery = true)
    List<Friendship> findRequestsSent(Long id, boolean approved);

    @Query(value="SELECT * FROM friendship WHERE approved=?2 AND receiver_id=?1", nativeQuery = true)
    List<Friendship> findRequestsRecieved(Long id, boolean approved);

    @Query(value="SELECT * FROM friendship WHERE (receiver_id=?1 AND sender_id=?2) OR (receiver_id=?2 AND sender_id=?1)", nativeQuery = true)
    Optional<Friendship> findFriendship(long friendOneId, long friendTwoId);
}