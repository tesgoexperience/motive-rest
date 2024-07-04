package com.motive.rest.motive.status;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.UUID;

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
@Table(uniqueConstraints = {@UniqueConstraint(name = "DUPLICATE_INTEREST_ENTRY", columnNames = { "status_id", "user_id" }) })
public class Interest {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;
    
    @ManyToOne
    Status status;
    @ManyToOne
    User user;

    public Interest(Status status,User user) {
        this.status=status;
        this.user=user;
    }
}
