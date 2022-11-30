package com.motive.rest.user;

import javax.persistence.Entity;
import javax.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.motive.rest.user.friendship.Friendship;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

import javax.persistence.Column;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@JsonIgnoreType
public class User {

    // CHANGE TO GUID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NonNull
    @Column(name = "email", unique = true)
    private String email;

    @NonNull
    @Column(name = "username", unique = true)
    private String username;

    @NonNull
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String password;

    private String[] roles;

    private boolean verified;

    @JsonIgnore
    @OneToMany(mappedBy = "requester", fetch = FetchType.LAZY)
    private List<Friendship> requestsMade;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Friendship> requestsReceived;

    public User(String email, String password) {
        this.email = email;
        verified = false;
        this.password = password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getPassword() {
        return this.password;
    }
}
