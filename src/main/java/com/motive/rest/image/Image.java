package com.motive.rest.image;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Image {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @Lob
    @Column(name = "data", columnDefinition = "longblob", nullable = true) //TODO limit size to 16 MB max
    private byte[] data;
    @Column(name = "type", nullable = true)
    private String type;

    public Image(byte[] data, String type) {
        this.data = data;
        this.type = type;
    }
}
