package com.careerradar.api;

import com.careerradar.job.Job;
import com.careerradar.matching.JobSkill;
import com.careerradar.matching.JobSkillExtractionService;
import com.careerradar.matching.JobSkillQueryService;
import com.careerradar.matching.Skill;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JobSkillControllerTest {

    @Test
    void shouldReturnJobSkills() {

        JobSkillExtractionService extractionService =
                mock(JobSkillExtractionService.class);

        JobSkillQueryService queryService =
                mock(JobSkillQueryService.class);

        Job job =
                mock(Job.class);

        Skill java =
                mock(Skill.class);

        Skill springBoot =
                mock(Skill.class);

        JobSkill first =
                mock(JobSkill.class);

        JobSkill second =
                mock(JobSkill.class);

        when(job.getId())
                .thenReturn(100L);

        when(java.getId())
                .thenReturn(1L);

        when(java.getName())
                .thenReturn("Java");

        when(java.getNormalizedName())
                .thenReturn("java");

        when(springBoot.getId())
                .thenReturn(2L);

        when(springBoot.getName())
                .thenReturn("Spring Boot");

        when(springBoot.getNormalizedName())
                .thenReturn("spring_boot");

        when(first.getJob())
                .thenReturn(job);

        when(first.getSkill())
                .thenReturn(java);

        when(first.getRequired())
                .thenReturn(true);

        when(first.getConfidence())
                .thenReturn(BigDecimal.valueOf(0.95));

        when(first.getSource())
                .thenReturn("RULE_BASED");

        when(second.getJob())
                .thenReturn(job);

        when(second.getSkill())
                .thenReturn(springBoot);

        when(second.getRequired())
                .thenReturn(false);

        when(second.getConfidence())
                .thenReturn(BigDecimal.valueOf(0.70));

        when(second.getSource())
                .thenReturn("RULE_BASED");

        when(queryService.getSkillsForJob(100L))
                .thenReturn(List.of(first, second));

        JobSkillController controller =
                new JobSkillController(
                        extractionService,
                        queryService
                );

        List<JobSkillResponse> result =
                controller.getJobSkills(100L);

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                100L,
                result.get(0).jobId()
        );

        assertEquals(
                "Java",
                result.get(0).skillName()
        );

        assertEquals(
                "java",
                result.get(0).normalizedSkillName()
        );

        assertEquals(
                true,
                result.get(0).required()
        );

        assertEquals(
                BigDecimal.valueOf(0.95),
                result.get(0).confidence()
        );

        assertEquals(
                "Spring Boot",
                result.get(1).skillName()
        );

        assertEquals(
                false,
                result.get(1).required()
        );

        verify(queryService)
                .getSkillsForJob(100L);
    }

    @Test
    void shouldExtractSkillsForJob() {

        JobSkillExtractionService extractionService =
                mock(JobSkillExtractionService.class);

        JobSkillQueryService queryService =
                mock(JobSkillQueryService.class);

        when(extractionService.extractJob(100L))
                .thenReturn(5);

        JobSkillController controller =
                new JobSkillController(
                        extractionService,
                        queryService
                );

        JobSkillController.ExtractJobResponse response =
                controller.extractJob(100L);

        assertEquals(
                100L,
                response.jobId()
        );

        assertEquals(
                5,
                response.extracted()
        );

        verify(extractionService)
                .extractJob(100L);
    }

    @Test
    void shouldExtractSkillsForAllJobs() {

        JobSkillExtractionService extractionService =
                mock(JobSkillExtractionService.class);

        JobSkillQueryService queryService =
                mock(JobSkillQueryService.class);

        when(extractionService.extractAllJobs())
                .thenReturn(25);

        JobSkillController controller =
                new JobSkillController(
                        extractionService,
                        queryService
                );

        JobSkillController.ExtractAllResponse response =
                controller.extractAll();

        assertEquals(
                25,
                response.extracted()
        );

        verify(extractionService)
                .extractAllJobs();
    }
}