package com.careerradar.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileLocationRepository
        extends JpaRepository<ProfileLocation, Long> {

    List<ProfileLocation>
    findByProfileIdAndActiveTrueOrderByPriorityAsc(
            Long profileId
    );
}