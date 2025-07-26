package com.hangout.core.profile_api.repo;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hangout.core.profile_api.model.Profile;
import com.hangout.core.profile_api.template.PublicProfileProjection;

public interface ProfileRepo extends JpaRepository<Profile, UUID> {

    @Query(value = "select name, profile_picture from profile where user_id = :userId", nativeQuery = true)
    Optional<PublicProfileProjection> findPublicProfileDetails(@Param("userId") BigInteger userId);

    Optional<Profile> findByUserId(BigInteger userId);

}
