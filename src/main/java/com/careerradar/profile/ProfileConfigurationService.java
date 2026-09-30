package com.careerradar.profile;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileConfigurationService {

    private final ProfileRepository profileRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final ProfileSkillRepository profileSkillRepository;

    public ProfileConfigurationService(
            ProfileRepository profileRepository,
            ProfileRoleRepository profileRoleRepository,
            ProfileSkillRepository profileSkillRepository) {

        this.profileRepository = profileRepository;
        this.profileRoleRepository = profileRoleRepository;
        this.profileSkillRepository = profileSkillRepository;
    }

    public Profile getActiveProfile() {
        return profileRepository.findAll()
                .stream()
                .filter(Profile::getActive)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No active Career Radar profile configured"));
    }

    public List<ProfileRole> getRoles(Long profileId) {
        return profileRoleRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(profileId);
    }

    public List<ProfileSkill> getSkills(Long profileId) {
        return profileSkillRepository
                .findByProfileIdAndActiveTrue(profileId);
    }
}