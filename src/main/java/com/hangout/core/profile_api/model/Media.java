package com.hangout.core.profile_api.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @JsonManagedReference
    @OneToMany(mappedBy = "profilePicture")
    private List<Profile> profiles;
    @JsonIgnore
    @Enumerated(value = EnumType.STRING)
    private ProcessStatus processStatus;

    public Media(String hashedFilename, String contentType) {
        this.filename = hashedFilename;
        this.contentType = contentType;
        this.profiles = new ArrayList<>();
        this.processStatus = ProcessStatus.IN_QUEUE;
    }

    public void addPost(Profile post) {
        if (this.profiles.isEmpty()) {
            this.processStatus = ProcessStatus.IN_QUEUE;
            this.profiles = new ArrayList<>();
        }
        this.profiles.add(post);
    }
}