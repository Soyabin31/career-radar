package com.careerradar.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfilePreferenceQueryServiceTest {

    @Test
    void shouldReturnActiveProfilePreferences() {

        ProfileConfigurationService
                profileConfigurationService =
                mock(ProfileConfigurationService.class);

        ProfileLocationRepository
                locationRepository =
                mock(ProfileLocationRepository.class);

        ProfileWorkplacePreferenceRepository
                workplaceRepository =
                mock(ProfileWorkplacePreferenceRepository.class);

        ProfileEmploymentPreferenceRepository
                employmentRepository =
                mock(ProfileEmploymentPreferenceRepository.class);

        ProfilePreferenceQueryService service =
                new ProfilePreferenceQueryService(
                        profileConfigurationService,
                        locationRepository,
                        workplaceRepository,
                        employmentRepository
                );

        Profile profile =
                mock(Profile.class);

        when(profile.getId())
                .thenReturn(1L);

        when(profile.getName())
                .thenReturn(
                        "Primary Career Profile"
                );

        ProfileLocation bengaluru =
                mock(ProfileLocation.class);

        when(bengaluru.getGeographyType())
                .thenReturn(
                        com.careerradar.job.domain.GeographyType.INDIA
                );

        when(bengaluru.getForeignRegion())
                .thenReturn(null);

        when(bengaluru.getCountry())
                .thenReturn("India");

        when(bengaluru.getStateOrRegion())
                .thenReturn(null);

        when(bengaluru.getCity())
                .thenReturn("Bengaluru");

        when(bengaluru.getPriority())
                .thenReturn(1);

        ProfileWorkplacePreference remote =
                mock(ProfileWorkplacePreference.class);

        when(remote.getWorkplaceType())
                .thenReturn(
                        com.careerradar.job.domain.WorkplaceType.REMOTE
                );

        ProfileEmploymentPreference fullTime =
                mock(ProfileEmploymentPreference.class);

        when(fullTime.getEmploymentType())
                .thenReturn(
                        com.careerradar.job.domain.EmploymentType.FULL_TIME
                );

        when(profileConfigurationService
                .getActiveProfile())
                .thenReturn(profile);

        when(locationRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of(bengaluru)
                );

        when(workplaceRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of(remote)
                );

        when(employmentRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of(fullTime)
                );

        var result =
                service.getActiveProfilePreferences();

        assertEquals(
                1L,
                result.profileId()
        );

        assertEquals(
                "Primary Career Profile",
                result.profileName()
        );

        assertEquals(
                1,
                result.locations().size()
        );

        assertEquals(
                "INDIA",
                result.locations()
                        .get(0)
                        .geographyType()
        );

        assertEquals(
                "India",
                result.locations()
                        .get(0)
                        .country()
        );

        assertEquals(
                "Bengaluru",
                result.locations()
                        .get(0)
                        .city()
        );

        assertEquals(
                List.of("REMOTE"),
                result.workplaceTypes()
        );

        assertEquals(
                List.of("FULL_TIME"),
                result.employmentTypes()
        );

        verify(
                profileConfigurationService
        ).getActiveProfile();

        verify(
                locationRepository
        ).findByProfileIdAndActiveTrueOrderByPriorityAsc(1L);

        verify(
                workplaceRepository
        ).findByProfileIdAndActiveTrueOrderByPriorityAsc(1L);

        verify(
                employmentRepository
        ).findByProfileIdAndActiveTrueOrderByPriorityAsc(1L);
    }
}