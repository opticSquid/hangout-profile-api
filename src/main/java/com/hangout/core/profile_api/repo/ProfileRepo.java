package com.hangout.core.profile_api.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hangout.core.profile_api.model.Profile;

public interface ProfileRepo extends JpaRepository<Profile, UUID> {

}
