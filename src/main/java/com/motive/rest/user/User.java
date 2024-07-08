package com.motive.rest.user;

import jakarta.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.motive.rest.Auth.AuthDetails;
import com.motive.rest.image.Image;

import lombok.*;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;

@Entity 
@JsonIgnoreProperties 
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @Column(name = "username", unique = true)
    private String username;

    @ElementCollection
    private Set<User> hideStatusFrom;

    @OneToOne(mappedBy = "owner",cascade = CascadeType.ALL)
    AuthDetails authDetails;
    
    @OneToOne
    Image profilePic;

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
