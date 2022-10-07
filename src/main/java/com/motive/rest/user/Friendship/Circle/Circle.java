package com.motive.rest.user.Friendship.Circle;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity

//TODO TEST check if this constraint holds
//TODO find other entites that require these constraints
@Table(uniqueConstraints = { @UniqueConstraint(name = CircleService.DUPLICATE_ENTRY, columnNames = { "owner", "name" }) })
public class Circle {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    String name;

    @OneToMany
    Set<User> members;

    @ManyToOne
    @JoinColumn(name="owner")
    User owner;

    String color;

    public Circle(User owner, String name, String color) {
        this.color = color;
        this.owner = owner;
        this.name = name;
        this.members = new HashSet<>();
    }
}
