package com.motive.rest.motive.status;

import java.util.List;
import java.util.UUID;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.motive.rest.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@JsonIgnoreType
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Status {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;
    
    @ManyToOne
    private User owner;
    
    @Lob
    @Column(name = "description", length = 400)
    private  String title;

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    private List<Interest> interest;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;

    public Status(String title, User owner) {
        this.title = title;
        this.owner = owner;
    }
}
