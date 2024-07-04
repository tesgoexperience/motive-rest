package com.motive.rest.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.chat.message.Message;
import com.motive.rest.motive.Motive;
import com.motive.rest.user.User;
import com.motive.rest.user.friendship.Friendship;

@JsonIgnoreType
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public enum TYPE {
        MOTIVE, FRIENDSHIP
    }
    @ManyToMany
    private List<User> members;
    @ManyToMany
    private List<User> notUpToDate;
    private TYPE type;
    @OneToOne(cascade = CascadeType.ALL)
    private Friendship belongsToFriendship;
    @OneToOne(cascade = CascadeType.ALL)
    private Motive belongsToMotive;
    
    @OneToOne
    private Message headMessage;
    
    public Chat(List<User> members, Friendship friendship) {
        this.notUpToDate = new ArrayList<>();

        this.type = TYPE.FRIENDSHIP;
        this.members = members;
        this.belongsToMotive = null;
        this.belongsToFriendship = friendship;
    }

    public Chat(User owner, Motive motive) {
        this.notUpToDate = new ArrayList<>();

        this.type = TYPE.MOTIVE;
        this.members = Arrays.asList(owner);
        this.belongsToMotive = motive;
        this.belongsToFriendship = null;
    }

}
