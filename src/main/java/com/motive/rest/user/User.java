package com.motive.rest.user;

import javax.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.motive.rest.Auth.AuthDetails;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;

@Entity @Data @JsonIgnoreProperties @NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    private UUID id;

    @NonNull
    @Column(name = "username", unique = true)
    private String username;


    @ElementCollection
    private Set<User> hideStatusFrom;

    @OneToOne(mappedBy = "owner",cascade = CascadeType.ALL)
    AuthDetails authDetails;

    public User(String username) {
        this.username = username;
        this.hideStatusFrom = new HashSet<>();
    }

}
