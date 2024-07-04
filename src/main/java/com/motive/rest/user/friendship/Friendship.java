package com.motive.rest.user.friendship;

import jakarta.persistence.Entity;

import com.motive.rest.chat.Chat;
import com.motive.rest.user.User;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@EqualsAndHashCode
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="sender_id", nullable=false)
    private User sender;

    @ManyToOne
    @JoinColumn(name="receiver_id", nullable=false)
    private User receiver;

    private boolean approved;

    @OneToOne(mappedBy = "belongsToFriendship")
    private Chat chat;

    public Friendship(User sender, User receiver) {
        this.receiver = receiver;
        this.sender = sender;
        approved = false;
    }

}

