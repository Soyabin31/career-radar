package com.careerradar.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileWorkplacePreferenceRepository
        extends JpaRepository<
        ProfileWorkplacePreference,
        Long> {

    List<ProfileWorkplacePreference>
    findByProfileIdAndActiveTrueOrderByPriorityAsc(
            Long profileId
    );
}