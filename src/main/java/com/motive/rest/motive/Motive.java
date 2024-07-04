package com.motive.rest.motive;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.chat.Chat;
import com.motive.rest.motive.Invite.Invite;
import com.motive.rest.motive.attendance.Attendance;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import java.util.List;
import java.util.UUID;
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
        EVERYONE/* TODO Change to friends of friends or change to friends of friends*/, FRIENDS, SPECIFIC_FRIENDS
    }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String title;

    @ManyToOne
    private User owner;
    @Lob
    @Column(name = "description", length = 50000)
    private String description;
    private Date start;
    private Date end;
    @OneToMany(mappedBy = "motive")
    private List<Attendance> attendance;

    @OneToMany(mappedBy = "motive", cascade = CascadeType.ALL)
    private List<Invite> specificallyInvited;

    private boolean cancelled;

    private ATTENDANCE_TYPE attendanceType;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    @OneToOne(mappedBy = "belongsToMotive")
    private Chat chat;

    public Motive(User owner, String title, String description, Date start, Date end, ATTENDANCE_TYPE type) {
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.attendance = new ArrayList<>();
        this.specificallyInvited = new ArrayList<>();
        this.cancelled = false;
        this.attendanceType = type;
    }
}
