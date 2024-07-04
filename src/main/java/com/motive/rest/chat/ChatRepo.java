package com.motive.rest.chat;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ChatRepo  extends CrudRepository<Chat,UUID>{
    @Query(value="SELECT * FROM chat INNER JOIN chat_members ON chat_members.chat_id = chat.id WHERE chat_members.members_id = ?1", nativeQuery=true)
    List<Chat> getChats(UUID user);

}
 