package com.careerradar.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository
        extends JpaRepository<Profile, Long> {

    Optional<Profile> findByNameIgnoreCase(String name);
}