package com.careerradar.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Long> {

    List<ProfileSkill> findByProfileIdAndActiveTrue(Long profileId);
}