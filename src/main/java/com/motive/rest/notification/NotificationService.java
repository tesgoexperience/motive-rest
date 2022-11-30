package com.motive.rest.notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.motive.rest.user.User;
import com.motive.rest.user.UserService;

@Service
public class NotificationService {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificationRepo repo;

    // get unacknowledged notifications
    public List<Notification> getUnacknowledgedNotifications(){
        return   repo.findByRecipientAndAcknowledged(userService.getCurrentUser(), false);
    }

    // creates a new notification
    public void notify (User user, String message, boolean push){
        // create new notification

        if (push) {
            // call push notification server and push
        }
    }

    // add a notification

}
