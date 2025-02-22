package com.hangout.core.profile_api.model;

import java.math.BigInteger;
import java.util.UUID;

import jakarta.persistence.Entity;
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
    private BigInteger userId;
    private String name;
    @ManyToOne
    @JoinColumn(name = "profile_picture")
    private Media profilePicture;

    public Profile(BigInteger userId, String name, Media profilePicture) {
        this.userId = userId;
        this.name = name;
        this.profilePicture = profilePicture;
    }
}
