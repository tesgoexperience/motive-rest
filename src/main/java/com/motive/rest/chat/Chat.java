package com.motive.rest.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    private UUID id;

    public enum TYPE {
        MOTIVE, FRIENDSHIP
    }

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages;
    @ManyToMany
    private List<User> members;
    @OneToMany
    private List<User> notUpToDate;
    private TYPE type;
    @OneToOne(cascade = CascadeType.ALL)
    private Friendship belongsToFriendship;
    @OneToOne(cascade = CascadeType.ALL)
    private Motive belongsToMotive;

    public Chat(List<User> members, Friendship friendship) {
        this.messages = new ArrayList<>();
        this.notUpToDate = new ArrayList<>();

        this.type = TYPE.FRIENDSHIP;
        this.members = members;
        this.belongsToMotive = null;
        this.belongsToFriendship = friendship;
    }

    public Chat(List<User> members, Motive motive) {
        this.messages = new ArrayList<>();
        this.notUpToDate = new ArrayList<>();

        this.type = TYPE.MOTIVE;
        this.belongsToMotive = motive;
        this.belongsToFriendship = null;
    }

}
