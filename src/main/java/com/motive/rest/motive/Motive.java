package com.motive.rest.motive;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.motive.Invite.Invite;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

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

    public enum ATTENDANCE_TYPE {
        EVERYONE, FRIENDS, SPECIFIC_FRIENDS
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String title;

    @ManyToOne
    private User owner;
    @Lob
    @Column(name = "description", length = 50000)
    private String description;
    private Date start;

    @OneToMany(mappedBy = "motive")
    private List<Attendance> attendance;

    @OneToMany(mappedBy = "motive", cascade = CascadeType.ALL)
    private List<Invite> specificallyInvited;

    private boolean finished;

    private ATTENDANCE_TYPE attendanceType;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    public Motive(User owner, String title, String description, Date start, ATTENDANCE_TYPE type) {
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.start = start;
        this.attendance = new ArrayList<>();
        this.specificallyInvited = new ArrayList<>();
        this.finished = false;
        this.attendanceType = type;
    }
}
