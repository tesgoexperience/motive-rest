package com.motive.rest.motive.attendance;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.motive.Motive;
import com.motive.rest.user.User;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@JsonIgnoreType
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(uniqueConstraints = {@UniqueConstraint(name = "DUPLICATE_ENTRY", columnNames = { "motive_id", "user_id" }) })
public class Attendance {

    public enum ATTENDANCE_STATUS {CONFIRMED, REQUESTED}

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    private Motive motive;

    @ManyToOne
    private User user;
    
    private ATTENDANCE_STATUS status;
    private boolean anonymous;

    public Attendance(User user, Motive motive, boolean anonymous) {
        this.motive = motive;
        this.user = user;
        this.status = ATTENDANCE_STATUS.REQUESTED;
        this.anonymous = anonymous;
    }

    public Attendance(User user, Motive motive) {
        this.motive = motive;
        this.user = user;
        this.status = ATTENDANCE_STATUS.REQUESTED;
        this.anonymous = false;
    }
}
