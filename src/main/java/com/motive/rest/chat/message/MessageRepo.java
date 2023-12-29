package com.motive.rest.chat.message;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
public interface MessageRepo extends PagingAndSortingRepository<Message,Long>{
    List<Message> findByChatIdOrderByCreateDateAsc(UUID chatId,Pageable pageable); 
}
