package com.motive.rest.Auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;



import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.motive.rest.user.User;


@Entity @Data @JsonIgnoreProperties @NoArgsConstructor
public class AuthDetails implements UserDetails {


    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;
    
    @OneToOne
    private User owner;

    private String email;
    private String password;
    private String notificationToken;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    String[] permissions;

    public AuthDetails(String email, String password, User owner) {
        this.email = email;
        this.password = password;
        this.owner = owner;

        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.isCredentialsNonExpired  = true;
        this.isEnabled = true;
        this.notificationToken = "";
        permissions = new String[]{"USER"};
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
