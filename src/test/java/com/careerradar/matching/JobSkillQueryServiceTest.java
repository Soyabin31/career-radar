package com.careerradar.matching;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JobSkillQueryServiceTest {

    @Test
    void shouldReturnSkillsForJob() {

        JobSkillRepository repository =
                mock(JobSkillRepository.class);

        JobSkill first =
                mock(JobSkill.class);

        JobSkill second =
                mock(JobSkill.class);

        when(repository.findByJobIdOrderByConfidenceDesc(100L))
                .thenReturn(List.of(first, second));

        JobSkillQueryService service =
                new JobSkillQueryService(repository);

        List<JobSkill> result =
                service.getSkillsForJob(100L);

        assertEquals(
                2,
                result.size()
        );

        verify(repository)
                .findByJobIdOrderByConfidenceDesc(100L);
    }

    @Test
    void shouldReturnEmptyListWhenJobHasNoSkills() {

        JobSkillRepository repository =
                mock(JobSkillRepository.class);

        when(repository.findByJobIdOrderByConfidenceDesc(100L))
                .thenReturn(List.of());

        JobSkillQueryService service =
                new JobSkillQueryService(repository);

        List<JobSkill> result =
                service.getSkillsForJob(100L);

        assertEquals(
                0,
                result.size()
        );

        verify(repository)
                .findByJobIdOrderByConfidenceDesc(100L);
    }
}