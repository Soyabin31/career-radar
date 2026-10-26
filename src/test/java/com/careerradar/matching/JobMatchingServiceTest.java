package com.careerradar.matching;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.RoleFamily;
import com.careerradar.profile.Profile;
import com.careerradar.profile.ProfileConfigurationService;
import com.careerradar.profile.ProfileRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobMatchingServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobRoleClassificationRepository
            roleClassificationRepository;

    @Mock
    private JobSkillRepository jobSkillRepository;

    @Mock
    private ProfileConfigurationService
            profileConfigurationService;

    @Mock
    private Profile profile;

    private JobMatchingService service;

    @BeforeEach
    void setUp() {

        service =
                new JobMatchingService(
                        jobRepository,
                        roleClassificationRepository,
                        jobSkillRepository,
                        profileConfigurationService
                );
    }

    @Test
    void unknownRoleMustNotReceiveRoleMatch() {

        Job job =
                createJob(
                        1L,
                        "Operations Lead"
                );

        when(jobRepository.findById(1L))
                .thenReturn(Optional.of(job));

        when(profileConfigurationService
                .getActiveProfile())
                .thenReturn(profile);

        when(profile.getId())
                .thenReturn(1L);

        /*
         * No ProfileRole is required here.
         *
         * UNKNOWN must short-circuit role matching before
         * profile roles are inspected.
         */
        when(profileConfigurationService
                .getRoles(1L))
                .thenReturn(List.of());

        when(profileConfigurationService
                .getSkills(1L))
                .thenReturn(List.of());

        /*
         * No persisted classification means UNKNOWN.
         */
        when(roleClassificationRepository
                .findByJobIdOrderByConfidenceDesc(1L))
                .thenReturn(List.of());

        when(jobSkillRepository
                .findByJobIdOrderByConfidenceDesc(1L))
                .thenReturn(List.of());

        JobMatchResult result =
                service.matchJob(1L);

        assertThat(result.jobRoleFamily())
                .isEqualTo("UNKNOWN");

        assertThat(result.roleMatch())
                .isFalse();

        assertThat(result.score())
                .isEqualByComparingTo("0.00");
    }

    @Test
    void javaBackendRoleShouldMatchJavaBackendProfile() {

        Job job =
                createJob(
                        2L,
                        "Senior Java Backend Engineer"
                );

        when(jobRepository.findById(2L))
                .thenReturn(Optional.of(job));

        when(profileConfigurationService
                .getActiveProfile())
                .thenReturn(profile);

        when(profile.getId())
                .thenReturn(1L);

        ProfileRole javaRole =
                mock(ProfileRole.class);

        when(javaRole.getActive())
                .thenReturn(true);

        when(javaRole.getRoleFamily())
                .thenReturn(RoleFamily.JAVA_BACKEND);

        when(profileConfigurationService
                .getRoles(1L))
                .thenReturn(List.of(javaRole));

        when(profileConfigurationService
                .getSkills(1L))
                .thenReturn(List.of());

        JobRoleClassification classification =
                mock(JobRoleClassification.class);

        when(classification.getRoleFamily())
                .thenReturn(RoleFamily.JAVA_BACKEND);

        when(roleClassificationRepository
                .findByJobIdOrderByConfidenceDesc(2L))
                .thenReturn(List.of(classification));

        when(jobSkillRepository
                .findByJobIdOrderByConfidenceDesc(2L))
                .thenReturn(List.of());

        JobMatchResult result =
                service.matchJob(2L);

        assertThat(result.jobRoleFamily())
                .isEqualTo("JAVA_BACKEND");

        assertThat(result.roleMatch())
                .isTrue();

        assertThat(result.score())
                .isEqualByComparingTo("60.00");
    }

    private Job createJob(
            Long id,
            String title) {

        Job job =
                mock(Job.class);

        when(job.getId())
                .thenReturn(id);

        when(job.getTitle())
                .thenReturn(title);

        return job;
    }
}