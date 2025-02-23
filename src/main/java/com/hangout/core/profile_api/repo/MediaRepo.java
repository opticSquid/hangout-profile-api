package com.hangout.core.profile_api.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hangout.core.profile_api.model.Media;

public interface MediaRepo extends JpaRepository<Media, String> {

}