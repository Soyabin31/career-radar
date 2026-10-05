package com.careerradar.eligibility;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.EligibilityStatus;
import com.careerradar.job.domain.EmploymentType;
import com.careerradar.job.domain.ForeignRegion;
import com.careerradar.job.domain.GeographyType;
import com.careerradar.job.domain.WorkplaceType;
import com.careerradar.profile.Profile;
import com.careerradar.profile.ProfileConfigurationService;
import com.careerradar.profile.ProfileEmploymentPreference;
import com.careerradar.profile.ProfileEmploymentPreferenceRepository;
import com.careerradar.profile.ProfileLocation;
import com.careerradar.profile.ProfileLocationRepository;
import com.careerradar.profile.ProfileWorkplacePreference;
import com.careerradar.profile.ProfileWorkplacePreferenceRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EligibilityServiceTest {

    @Test
    void shouldMarkMatchingIndiaRemoteFullTimeJobAsRegionOk() {

        JobRepository jobRepository =
                mock(JobRepository.class);

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

        EligibilityService service =
                new EligibilityService(
                        jobRepository,
                        profileConfigurationService,
                        locationRepository,
                        workplaceRepository,
                        employmentRepository
                );

        Job job =
                mock(Job.class);

        Profile profile =
                mock(Profile.class);

        ProfileLocation location =
                mock(ProfileLocation.class);

        ProfileWorkplacePreference workplace =
                mock(ProfileWorkplacePreference.class);

        ProfileEmploymentPreference employment =
                mock(ProfileEmploymentPreference.class);

        when(job.getId())
                .thenReturn(100L);

        when(job.getGeographyType())
                .thenReturn(GeographyType.INDIA);

        when(job.getCountry())
                .thenReturn("India");

        when(job.getCity())
                .thenReturn("Bengaluru");

        when(job.getWorkplaceType())
                .thenReturn(WorkplaceType.REMOTE);

        when(job.getEmploymentType())
                .thenReturn(EmploymentType.FULL_TIME);

        when(profile.getId())
                .thenReturn(1L);

        when(location.getGeographyType())
                .thenReturn(GeographyType.INDIA);

        when(location.getCountry())
                .thenReturn("India");

        when(location.getCity())
                .thenReturn("Bengaluru");

        when(location.getActive())
                .thenReturn(true);

        when(workplace.getWorkplaceType())
                .thenReturn(WorkplaceType.REMOTE);

        when(workplace.getActive())
                .thenReturn(true);

        when(employment.getEmploymentType())
                .thenReturn(EmploymentType.FULL_TIME);

        when(employment.getActive())
                .thenReturn(true);

        when(jobRepository.findById(100L))
                .thenReturn(
                        Optional.of(job)
                );

        when(profileConfigurationService
                .getActiveProfile())
                .thenReturn(profile);

        when(locationRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of(location)
                );

        when(workplaceRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of(workplace)
                );

        when(employmentRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of(employment)
                );

        EligibilityResult result =
                service.evaluateJob(100L);

        assertEquals(
                100L,
                result.jobId()
        );

        assertEquals(
                EligibilityStatus.REGION_OK,
                result.status()
        );

        assertTrue(
                result.geographyMatch()
        );

        assertTrue(
                result.workplaceMatch()
        );

        assertTrue(
                result.employmentMatch()
        );
    }

    @Test
    void shouldRejectWrongWorkplaceType() {

        JobRepository jobRepository =
                mock(JobRepository.class);

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

        EligibilityService service =
                new EligibilityService(
                        jobRepository,
                        profileConfigurationService,
                        locationRepository,
                        workplaceRepository,
                        employmentRepository
                );

        Job job =
                mock(Job.class);

        Profile profile =
                mock(Profile.class);

        ProfileLocation location =
                mock(ProfileLocation.class);

        ProfileWorkplacePreference workplace =
                mock(ProfileWorkplacePreference.class);

        when(job.getId())
                .thenReturn(200L);

        when(job.getGeographyType())
                .thenReturn(GeographyType.INDIA);

        when(job.getCountry())
                .thenReturn("India");

        when(job.getCity())
                .thenReturn("Bengaluru");

        when(job.getWorkplaceType())
                .thenReturn(WorkplaceType.ONSITE);

        when(job.getEmploymentType())
                .thenReturn(EmploymentType.FULL_TIME);

        when(profile.getId())
                .thenReturn(1L);

        when(location.getGeographyType())
                .thenReturn(GeographyType.INDIA);

        when(location.getCountry())
                .thenReturn("India");

        when(location.getCity())
                .thenReturn("Bengaluru");

        when(location.getActive())
                .thenReturn(true);

        when(workplace.getWorkplaceType())
                .thenReturn(WorkplaceType.REMOTE);

        when(workplace.getActive())
                .thenReturn(true);

        when(jobRepository.findById(200L))
                .thenReturn(
                        Optional.of(job)
                );

        when(profileConfigurationService
                .getActiveProfile())
                .thenReturn(profile);

        when(locationRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of(location)
                );

        when(workplaceRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of(workplace)
                );

        when(employmentRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of()
                );

        EligibilityResult result =
                service.evaluateJob(200L);

        assertEquals(
                EligibilityStatus.NOT_ELIGIBLE,
                result.status()
        );

        assertTrue(
                result.geographyMatch()
        );

        assertFalse(
                result.workplaceMatch()
        );
    }

    @Test
    void shouldRejectWrongForeignRegion() {

        JobRepository jobRepository =
                mock(JobRepository.class);

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

        EligibilityService service =
                new EligibilityService(
                        jobRepository,
                        profileConfigurationService,
                        locationRepository,
                        workplaceRepository,
                        employmentRepository
                );

        Job job =
                mock(Job.class);

        Profile profile =
                mock(Profile.class);

        ProfileLocation location =
                mock(ProfileLocation.class);

        when(job.getId())
                .thenReturn(300L);

        when(job.getGeographyType())
                .thenReturn(GeographyType.FOREIGN);

        when(job.getForeignRegion())
                .thenReturn(ForeignRegion.USA);

        when(job.getCountry())
                .thenReturn("USA");

        when(job.getWorkplaceType())
                .thenReturn(WorkplaceType.REMOTE);

        when(job.getEmploymentType())
                .thenReturn(EmploymentType.FULL_TIME);

        when(profile.getId())
                .thenReturn(1L);

        when(location.getGeographyType())
                .thenReturn(GeographyType.FOREIGN);

        when(location.getForeignRegion())
                .thenReturn(ForeignRegion.EUROPE);

        when(location.getActive())
                .thenReturn(true);

        when(jobRepository.findById(300L))
                .thenReturn(
                        Optional.of(job)
                );

        when(profileConfigurationService
                .getActiveProfile())
                .thenReturn(profile);

        when(locationRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of(location)
                );

        when(workplaceRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of()
                );

        when(employmentRepository
                .findByProfileIdAndActiveTrueOrderByPriorityAsc(1L))
                .thenReturn(
                        List.of()
                );

        EligibilityResult result =
                service.evaluateJob(300L);

        assertEquals(
                EligibilityStatus.NOT_ELIGIBLE,
                result.status()
        );

        assertFalse(
                result.geographyMatch()
        );
    }
}