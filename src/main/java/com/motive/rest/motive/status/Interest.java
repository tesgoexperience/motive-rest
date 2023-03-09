package com.motive.rest.motive.status;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@JsonIgnoreType
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(uniqueConstraints = {@UniqueConstraint(name = "DUPLICATE_INTEREST_ENTRY", columnNames = { "status_id", "user_id" }) })
public class Interest {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
    @ManyToOne
    Status status;
    @ManyToOne
    User user;

    public Interest(Status status,User user) {
        this.status=status;
        this.user=user;
    }
}
