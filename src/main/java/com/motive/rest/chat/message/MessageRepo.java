package com.motive.rest.chat.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
public interface MessageRepo extends PagingAndSortingRepository<Message,Long>{
    Optional<Message> findById(Long id);
    void save(Message m);
    List<Message> findByChatIdOrderByCreateDateAsc(UUID chatId,Pageable pageable); 
}
