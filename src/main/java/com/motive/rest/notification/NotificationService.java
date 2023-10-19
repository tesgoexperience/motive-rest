package com.motive.rest.notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.motive.rest.Auth.AuthService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;

import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.ExpoPushMessageTicketPair;
import io.github.jav.exposerversdk.ExpoPushTicket;
import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;

@Service
public class NotificationService {

    @Autowired
    AuthService authService;

    // creates a new notification
    public void notify(String title, String body, String token) {
        if (token == null || token.isEmpty()) {
            return;
        }

        if (!PushClient.isExponentPushToken(token))
            throw new Error("Token:" + token + " is not a valid token.");

        ExpoPushMessage expoPushMessage = new ExpoPushMessage();
        expoPushMessage.getTo().add(token);
        expoPushMessage.setTitle(title);
        expoPushMessage.setBody(body);

        List<ExpoPushMessage> expoPushMessages = new ArrayList<>();
        expoPushMessages.add(expoPushMessage);

        try {
            PushClient client = new PushClient();

            List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(expoPushMessages);

            List<CompletableFuture<List<ExpoPushTicket>>> messageRepliesFutures = new ArrayList<>();

            for (List<ExpoPushMessage> chunk : chunks) {
                messageRepliesFutures.add(client.sendPushNotificationsAsync(chunk));
            }

            // Wait for each completable future to finish
            List<ExpoPushTicket> allTickets = new ArrayList<>();
            for (CompletableFuture<List<ExpoPushTicket>> messageReplyFuture : messageRepliesFutures) {
                try {
                    for (ExpoPushTicket ticket : messageReplyFuture.get()) {
                        allTickets.add(ticket);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            List<ExpoPushMessageTicketPair<ExpoPushMessage>> zippedMessagesTickets = client
                    .zipMessagesTickets(expoPushMessages, allTickets);

            List<ExpoPushMessageTicketPair<ExpoPushMessage>> okTicketMessages = client
                    .filterAllSuccessfulMessages(zippedMessagesTickets);
            String okTicketMessagesString = okTicketMessages.stream().map(
                    p -> "Title: " + p.message.getTitle() + ", Id:" + p.ticket.getId())
                    .collect(Collectors.joining(","));
            System.out.println(
                    "Recieved OK ticket for " +
                            okTicketMessages.size() +
                            " messages: " + okTicketMessagesString);

            List<ExpoPushMessageTicketPair<ExpoPushMessage>> errorTicketMessages = client
                    .filterAllMessagesWithError(zippedMessagesTickets);
            String errorTicketMessagesString = errorTicketMessages.stream().map(
                    p -> "Title: " + p.message.getTitle() + ", Error: " + p.ticket.getDetails().getError())
                    .collect(Collectors.joining(","));
            System.out.println(
                    "Recieved ERROR ticket for " +
                            errorTicketMessages.size() +
                            " messages: " +
                            errorTicketMessagesString);
        } catch (PushClientException e) {
            e.printStackTrace();
        }
    }

}
