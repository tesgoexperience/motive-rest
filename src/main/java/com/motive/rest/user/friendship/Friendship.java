package com.motive.rest.user.friendship;

import javax.persistence.Entity;

import com.motive.rest.chat.Chat;
import com.motive.rest.user.User;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@EqualsAndHashCode
public class Friendship {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

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

