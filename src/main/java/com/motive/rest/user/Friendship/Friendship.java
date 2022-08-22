package com.motive.rest.user.Friendship;


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
public class Friendship {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    private User requester;

    @ManyToOne
    private User receiver;

    boolean approved;

    public Friendship(User requester, User friend) {
        this.receiver = friend;
        this.requester = requester;
        approved = false;
    }

}

