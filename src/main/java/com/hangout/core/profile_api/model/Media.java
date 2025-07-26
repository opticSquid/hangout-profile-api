package com.hangout.core.profile_api.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Media {
    @Id
    @Column(length = 513)
    private String filename;
    private String contentType;

    @OneToMany(mappedBy = "profilePicture")
    @JsonBackReference // Back side of the relationship
    private List<Profile> profiles;

    public Media(String hashedFilename, String contentType) {
        this.filename = hashedFilename;
        this.contentType = contentType;
        this.profiles = new ArrayList<>();
    }

    public void addPost(Profile post) {
        if (this.profiles.isEmpty()) {
            this.profiles = new ArrayList<>();
        }
        this.profiles.add(post);
    }
}