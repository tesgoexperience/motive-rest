package com.motive.rest.motive.attendance;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.motive.Motive;
import com.motive.rest.user.User;

import jakarta.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@JsonIgnoreType
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(uniqueConstraints = {@UniqueConstraint(name = "DUPLICATE_ATTENDANCE_ENTRY", columnNames = { "motive_id", "user_id" }) })
public class Attendance {

    public enum ATTENDANCE_STATUS {CONFIRMED, REQUESTED}

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

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
