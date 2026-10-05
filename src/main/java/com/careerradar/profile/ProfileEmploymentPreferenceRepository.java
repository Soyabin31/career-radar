package com.careerradar.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileEmploymentPreferenceRepository
        extends JpaRepository<
        ProfileEmploymentPreference,
        Long> {

    List<ProfileEmploymentPreference>
    findByProfileIdAndActiveTrueOrderByPriorityAsc(
            Long profileId
    );
}