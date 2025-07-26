package com.hangout.core.profile_api.model;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID profileId;
    @Column(unique = true)
    private BigInteger userId;
    private String name;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private ZonedDateTime dob;
    @ManyToOne
    @JoinColumn(name = "profile_picture")
    @JsonManagedReference // Managed side of the relationship
    private Media profilePicture;

    public Profile(BigInteger userId, String name, Gender gender, ZonedDateTime dob, Media profilePicture) {
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.profilePicture = profilePicture;
    }
}
