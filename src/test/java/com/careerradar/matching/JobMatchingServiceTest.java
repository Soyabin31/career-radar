package com.careerradar.matching;

import com.careerradar.job.Job;
import com.careerradar.job.JobRepository;
import com.careerradar.job.domain.RoleFamily;
import com.careerradar.profile.Profile;
import com.careerradar.profile.ProfileConfigurationService;
import com.careerradar.profile.ProfileRole;
import com.careerradar.profile.ProfileSkill;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobMatchingServiceTest {

    @Test
    void shouldMatchJavaBackendJob() {

        JobRepository jobRepository =
                mock(JobRepository.class);

        JobRoleClassificationRepository
                classificationRepository =
                mock(JobRoleClassificationRepository.class);

        JobSkillRepository jobSkillRepository =
                mock(JobSkillRepository.class);

        ProfileConfigurationService
                profileConfigurationService =
                mock(ProfileConfigurationService.class);

        JobMatchingService service =
                new JobMatchingService(
                        jobRepository,
                        classificationRepository,
                        jobSkillRepository,
                        profileConfigurationService
                );

        Job job =
                mock(Job.class);

        when(job.getId())
                .thenReturn(100L);

        when(job.getTitle())
                .thenReturn(
                        "Senior Java Backend Engineer"
                );

        Profile profile =
                mock(Profile.class);

        when(profile.getId())
                .thenReturn(1L);

        ProfileRole javaBackendRole =
                mock(ProfileRole.class);

        when(javaBackendRole.getActive())
                .thenReturn(true);

        when(javaBackendRole.getRoleFamily())
                .thenReturn(
                        RoleFamily.JAVA_BACKEND
                );

        ProfileSkill javaSkill =
                mock(ProfileSkill.class);

        Skill java =
                mockSkill(
                        1L,
                        "Java",
                        "java"
                );

        when(javaSkill.getActive())
                .thenReturn(true);

        when(javaSkill.getSkill())
                .thenReturn(java);

        when(javaSkill.getSkillType())
                .thenReturn("PRIMARY");

        when(javaSkill.getWeight())
                .thenReturn(
                        BigDecimal.valueOf(1.50)
                );

        JobRoleClassification classification =
                mock(JobRoleClassification.class);

        when(classification.getRoleFamily())
                .thenReturn(
                        RoleFamily.JAVA_BACKEND
                );

        JobSkill jobSkill =
                mock(JobSkill.class);

        when(jobSkill.getSkill())
                .thenReturn(java);

        when(jobSkill.getRequired())
                .thenReturn(true);

        when(jobSkill.getConfidence())
                .thenReturn(
                        BigDecimal.valueOf(0.95)
                );

        when(jobRepository.findById(100L))
                .thenReturn(
                        Optional.of(job)
                );

        when(profileConfigurationService
                .getActiveProfile())
                .thenReturn(profile);

        when(profileConfigurationService
                .getRoles(1L))
                .thenReturn(
                        List.of(javaBackendRole)
                );

        when(profileConfigurationService
                .getSkills(1L))
                .thenReturn(
                        List.of(javaSkill)
                );

        when(classificationRepository
                .findByJobIdOrderByConfidenceDesc(100L))
                .thenReturn(
                        List.of(classification)
                );

        when(jobSkillRepository
                .findByJobIdOrderByConfidenceDesc(100L))
                .thenReturn(
                        List.of(jobSkill)
                );

        JobMatchResult result =
                service.matchJob(100L);

        assertEquals(
                100L,
                result.jobId()
        );

        assertEquals(
                "Senior Java Backend Engineer",
                result.jobTitle()
        );

        assertEquals(
                "JAVA_BACKEND",
                result.jobRoleFamily()
        );

        assertTrue(
                result.roleMatch()
        );

        /*
         * BigDecimal scale should not affect the assertion.
         *
         * Service returns 100.00 because scores are rounded
         * to two decimal places.
         */
        assertEquals(
                0,
                result.score().compareTo(
                        BigDecimal.valueOf(100)
                )
        );

        assertEquals(
                1,
                result.matchedSkills().size()
        );

        assertEquals(
                "Java",
                result.matchedSkills().get(0)
        );

        assertTrue(
                result.missingSkills().isEmpty()
        );
    }

    @Test
    void shouldNotMatchPythonBackendAsJavaBackend() {

        JobRepository jobRepository =
                mock(JobRepository.class);

        JobRoleClassificationRepository
                classificationRepository =
                mock(JobRoleClassificationRepository.class);

        JobSkillRepository jobSkillRepository =
                mock(JobSkillRepository.class);

        ProfileConfigurationService
                profileConfigurationService =
                mock(ProfileConfigurationService.class);

        JobMatchingService service =
                new JobMatchingService(
                        jobRepository,
                        classificationRepository,
                        jobSkillRepository,
                        profileConfigurationService
                );

        Job job =
                mock(Job.class);

        when(job.getId())
                .thenReturn(200L);

        when(job.getTitle())
                .thenReturn(
                        "Senior Python Backend Engineer"
                );

        Profile profile =
                mock(Profile.class);

        when(profile.getId())
                .thenReturn(1L);

        ProfileRole javaBackendRole =
                mock(ProfileRole.class);

        when(javaBackendRole.getActive())
                .thenReturn(true);

        when(javaBackendRole.getRoleFamily())
                .thenReturn(
                        RoleFamily.JAVA_BACKEND
                );

        ProfileSkill kubernetes =
                mock(ProfileSkill.class);

        Skill kubernetesSkill =
                mockSkill(
                        10L,
                        "Kubernetes",
                        "kubernetes"
                );

        when(kubernetes.getActive())
                .thenReturn(true);

        when(kubernetes.getSkill())
                .thenReturn(kubernetesSkill);

        when(kubernetes.getSkillType())
                .thenReturn("PRIMARY");

        when(kubernetes.getWeight())
                .thenReturn(
                        BigDecimal.valueOf(1.10)
                );

        JobRoleClassification pythonClassification =
                mock(JobRoleClassification.class);

        when(pythonClassification.getRoleFamily())
                .thenReturn(
                        RoleFamily.PYTHON_BACKEND
                );

        JobSkill pythonJobSkill =
                mock(JobSkill.class);

        when(pythonJobSkill.getSkill())
                .thenReturn(kubernetesSkill);

        when(pythonJobSkill.getRequired())
                .thenReturn(true);

        when(pythonJobSkill.getConfidence())
                .thenReturn(
                        BigDecimal.valueOf(0.95)
                );

        when(jobRepository.findById(200L))
                .thenReturn(Optional.of(job));

        when(profileConfigurationService
                .getActiveProfile())
                .thenReturn(profile);

        when(profileConfigurationService
                .getRoles(1L))
                .thenReturn(
                        List.of(javaBackendRole)
                );

        when(profileConfigurationService
                .getSkills(1L))
                .thenReturn(
                        List.of(kubernetes)
                );

        when(classificationRepository
                .findByJobIdOrderByConfidenceDesc(200L))
                .thenReturn(
                        List.of(pythonClassification)
                );

        when(jobSkillRepository
                .findByJobIdOrderByConfidenceDesc(200L))
                .thenReturn(
                        List.of(pythonJobSkill)
                );

        JobMatchResult result =
                service.matchJob(200L);

        assertFalse(
                result.roleMatch()
        );

        assertEquals(
                "PYTHON_BACKEND",
                result.jobRoleFamily()
        );

        assertTrue(
                result.score().doubleValue() < 50.0
        );

        assertEquals(
                1,
                result.matchedSkills().size()
        );

        assertEquals(
                "Kubernetes",
                result.matchedSkills().get(0)
        );
    }

    private Skill mockSkill(
            Long id,
            String name,
            String normalizedName) {

        Skill skill =
                mock(Skill.class);

        when(skill.getId())
                .thenReturn(id);

        when(skill.getName())
                .thenReturn(name);

        when(skill.getNormalizedName())
                .thenReturn(normalizedName);

        return skill;
    }
}