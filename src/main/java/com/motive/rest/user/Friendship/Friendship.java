package com.motive.rest.user.Friendship;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.HashCodeBuilder;
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Friendship {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name="requester_id", nullable=false)
    private User requester;

    @ManyToOne
    @JoinColumn(name="receiver_id", nullable=false)
    private User receiver;

    boolean approved;

    public Friendship(User requester, User friend) {
        this.receiver = friend;
        this.requester = requester;
        approved = false;
    }

    @Override
    public int hashCode(){
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(new HashSet<User>(Arrays.asList(this.getReceiver(), this.getRequester())));
        return hcb.toHashCode();

    }
    @Override
    public boolean equals(Object o){
        if (o == this) {
            return true;
        }

        if (!(o instanceof Friendship)) {
            return false;
        }

        Friendship otherFriendship = (Friendship)o;
        if ((this.id!=null && otherFriendship.id!=null) && this.id.equals(otherFriendship.id)) {
            return true;
        }

        Set<User> otherFriendshipSet = new HashSet<User>(Arrays.asList(otherFriendship.getReceiver(), otherFriendship.getRequester()));
        Set<User> friendshipSet = new HashSet<User>(Arrays.asList(this.getReceiver(), this.getRequester()));

        return otherFriendshipSet.equals(friendshipSet);

    }
}

