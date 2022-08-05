package com.motive.rest.user;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import javax.persistence.Column;

@Entity
@NoArgsConstructor @Getter @Setter
@ToString
@EqualsAndHashCode
public  class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id @JsonIgnore
    private Long id;

    @NonNull
    @Column(name="email", unique=true)
    private String  email;

    @NonNull
    @Column(name="username", unique=true)
    private String  username;

    @NonNull
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String password;

    @JsonIgnore
    private String[] roles;

    @JsonIgnore
    private boolean verified;

    protected User(String email, String password) {
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
