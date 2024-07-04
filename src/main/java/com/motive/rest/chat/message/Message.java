package com.motive.rest.chat.message;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.chat.Chat;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Entity;

@JsonIgnoreType
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Message {
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE
    )
    private Long id;
    @ManyToOne
    Chat chat;
    @Lob
    @Column(name = "content", length = 20000)
    String content;
    @ManyToOne
    User sender;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    public Message(Chat chat, String content, User sender) {
        this.chat = chat;
        this.content = content;
        this.sender = sender;
    }
}
