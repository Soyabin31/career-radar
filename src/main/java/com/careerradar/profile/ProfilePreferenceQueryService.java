package com.careerradar.profile;

import com.careerradar.api.ProfilePreferenceSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfilePreferenceQueryService {

    private final ProfileConfigurationService
            profileConfigurationService;

    private final ProfileLocationRepository
            profileLocationRepository;

    private final ProfileWorkplacePreferenceRepository
            workplacePreferenceRepository;

    private final ProfileEmploymentPreferenceRepository
            employmentPreferenceRepository;

    public ProfilePreferenceQueryService(
            ProfileConfigurationService
                    profileConfigurationService,
            ProfileLocationRepository
                    profileLocationRepository,
            ProfileWorkplacePreferenceRepository
                    workplacePreferenceRepository,
            ProfileEmploymentPreferenceRepository
                    employmentPreferenceRepository) {

        this.profileConfigurationService =
                profileConfigurationService;

        this.profileLocationRepository =
                profileLocationRepository;

        this.workplacePreferenceRepository =
                workplacePreferenceRepository;

        this.employmentPreferenceRepository =
                employmentPreferenceRepository;
    }

    @Transactional(readOnly = true)
    public ProfilePreferenceSummary
    getActiveProfilePreferences() {

        Profile profile =
                profileConfigurationService
                        .getActiveProfile();

        Long profileId =
                profile.getId();

        List<ProfilePreferenceSummary.LocationPreference>
                locations =
                profileLocationRepository
                        .findByProfileIdAndActiveTrueOrderByPriorityAsc(
                                profileId
                        )
                        .stream()
                        .map(this::toLocationPreference)
                        .toList();

        List<String> workplaceTypes =
                workplacePreferenceRepository
                        .findByProfileIdAndActiveTrueOrderByPriorityAsc(
                                profileId
                        )
                        .stream()
                        .map(preference ->
                                preference
                                        .getWorkplaceType()
                                        .name()
                        )
                        .toList();

        List<String> employmentTypes =
                employmentPreferenceRepository
                        .findByProfileIdAndActiveTrueOrderByPriorityAsc(
                                profileId
                        )
                        .stream()
                        .map(preference ->
                                preference
                                        .getEmploymentType()
                                        .name()
                        )
                        .toList();

        return new ProfilePreferenceSummary(
                profileId,
                profile.getName(),
                locations,
                workplaceTypes,
                employmentTypes
        );
    }

    private ProfilePreferenceSummary.LocationPreference
    toLocationPreference(
            ProfileLocation location) {

        return new ProfilePreferenceSummary.LocationPreference(
                location.getGeographyType() == null
                        ? null
                        : location.getGeographyType().name(),

                location.getForeignRegion() == null
                        ? null
                        : location.getForeignRegion().name(),

                location.getCountry(),

                location.getStateOrRegion(),

                location.getCity(),

                location.getPriority()
        );
    }
}