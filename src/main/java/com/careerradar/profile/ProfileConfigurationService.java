package com.careerradar.profile;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileConfigurationService {

    private final ProfileRepository profileRepository;
    private final ProfileRoleRepository profileRoleRepository;
    private final ProfileSkillRepository profileSkillRepository;

    private volatile Profile cachedProfile;
    private final java.util.Map<Long, List<ProfileRole>> rolesCache = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<Long, List<ProfileSkill>> skillsCache = new java.util.concurrent.ConcurrentHashMap<>();
    private volatile long profileCacheExpiry = 0;

    public ProfileConfigurationService(
            ProfileRepository profileRepository,
            ProfileRoleRepository profileRoleRepository,
            ProfileSkillRepository profileSkillRepository) {

        this.profileRepository = profileRepository;
        this.profileRoleRepository = profileRoleRepository;
        this.profileSkillRepository = profileSkillRepository;
    }

    public Profile getActiveProfile() {
        long now = System.currentTimeMillis();
        if (cachedProfile != null && now < profileCacheExpiry) {
            return cachedProfile;
        }

        Profile profile = profileRepository.findAll()
                .stream()
                .filter(Profile::getActive)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No active Career Radar profile configured"));

        cachedProfile = profile;
        profileCacheExpiry = now + 60_000;
        return profile;
    }

    public List<ProfileRole> getRoles(Long profileId) {
        long now = System.currentTimeMillis();
        if (now < profileCacheExpiry && rolesCache.containsKey(profileId)) {
            return rolesCache.get(profileId);
        }

        List<ProfileRole> roles = profileRoleRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(profileId);
        rolesCache.put(profileId, roles);
        return roles;
    }

    public List<ProfileSkill> getSkills(Long profileId) {
        long now = System.currentTimeMillis();
        if (now < profileCacheExpiry && skillsCache.containsKey(profileId)) {
            return skillsCache.get(profileId);
        }

        List<ProfileSkill> skills = profileSkillRepository
                .findByProfileIdAndActiveTrue(profileId);
        skillsCache.put(profileId, skills);
        return skills;
    }

    public void clearCache() {
        cachedProfile = null;
        rolesCache.clear();
        skillsCache.clear();
        profileCacheExpiry = 0;
    }
}