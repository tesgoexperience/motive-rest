package com.motive.rest.motive.Invite;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.motive.Motive;
import com.motive.rest.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@JsonIgnoreType
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(uniqueConstraints = {@UniqueConstraint(name = "DUPLICATE_INVITE_ENTRY", columnNames = { "motive_id", "user_id" }) })
public class Invite {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
    @ManyToOne
    Motive motive;
    @ManyToOne
    User user;

    public Invite(Motive motive,User user) {
        this.motive=motive;
        this.user=user;
    }
}
