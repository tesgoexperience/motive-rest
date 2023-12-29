package com.motive.rest.user;

import javax.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.motive.rest.Auth.AuthDetails;
import lombok.*;

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

// Removed @NoArgsConstructor annotation

@Entity @JsonIgnoreProperties @NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
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

    @Override public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User)o;
        return  user.getId().equals(this.id) && user.getUsername().equals(this.username) && user.getAuthDetails().getEmail().equals(this.authDetails.getEmail());
      }
}
