package com.motive.rest.motive;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.motive.attendance.Attendance;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@JsonIgnoreType
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Motive {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String title;

    @ManyToOne
    private User owner;
    @Lob
    @Column(name="description", length = 50000)
    private String description;
    private Date start;

    @OneToMany(mappedBy = "motive")
    private List<Attendance> attendance;
    @OneToMany
    private List<User> hiddenFrom; // move to CIRCLES
    private boolean finished;

    public Motive(User owner, String title, String description, Date start) {
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.start = start;
        this.attendance = new ArrayList<>();
        this.hiddenFrom = new ArrayList<>();
        this.finished = false;
    }
}
