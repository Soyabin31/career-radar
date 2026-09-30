package com.careerradar.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRoleRepository extends JpaRepository<ProfileRole, Long> {

    List<ProfileRole> findByProfileIdAndActiveTrueOrderByPriorityAsc(Long profileId);
}