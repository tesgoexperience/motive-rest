package com.motive.rest.user.Friend;


import javax.persistence.Entity;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
public class FriendRequest {

    public enum REQUEST_STATUS {
        PENDING, REJECTED, ACCEPTED, CANCELLED
    } 

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    User requester;
    @ManyToOne
    User friend;
    REQUEST_STATUS status;

    public FriendRequest(User requester, User friend) {
        this.friend = friend;
        this.requester = requester;
        this.status = REQUEST_STATUS.PENDING;
    }

}

