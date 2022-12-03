package com.motive.rest.notification;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.motive.rest.user.User;

public interface NotificationRepo extends CrudRepository<Notification, Long>{

    List<Notification> findByRecipientAndAcknowledged(User recipient, boolean acknowledged);
}

