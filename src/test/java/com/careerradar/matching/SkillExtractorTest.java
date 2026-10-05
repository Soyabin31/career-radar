package com.careerradar.matching;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillExtractorTest {

    private final SkillExtractor extractor =
            new SkillExtractor();

    @Test
    void shouldExtractJavaBackendSkills() {

        Skill java =
                mockSkill(1L, "Java", "java");

        Skill springBoot =
                mockSkill(
                        2L,
                        "Spring Boot",
                        "spring_boot"
                );

        Skill kubernetes =
                mockSkill(
                        3L,
                        "Kubernetes",
                        "kubernetes"
                );

        List<SkillExtractor.ExtractedSkill> result =
                extractor.extract(
                        "Senior Java Backend Engineer",
                        """
                        Required experience with Java and Spring Boot.
                        Kubernetes is nice to have.
                        """,
                        List.of(
                                java,
                                springBoot,
                                kubernetes
                        )
                );

        assertEquals(
                3,
                result.size()
        );

        var javaResult =
                result.stream()
                        .filter(item ->
                                item.skill().getNormalizedName()
                                        .equals("java"))
                        .findFirst()
                        .orElseThrow();

        assertTrue(
                javaResult.required()
        );

        var kubernetesResult =
                result.stream()
                        .filter(item ->
                                item.skill().getNormalizedName()
                                        .equals("kubernetes"))
                        .findFirst()
                        .orElseThrow();

        assertFalse(
                kubernetesResult.required()
        );
    }

    @Test
    void shouldExtractPythonEvenWhenJavaIsMentioned() {

        Skill python =
                mockSkill(
                        1L,
                        "Python",
                        "python"
                );

        Skill java =
                mockSkill(
                        2L,
                        "Java",
                        "java"
                );

        Skill kafka =
                mockSkill(
                        3L,
                        "Kafka",
                        "kafka"
                );

        List<SkillExtractor.ExtractedSkill> result =
                extractor.extract(
                        "Senior Python Backend Engineer",
                        """
                        Required: Python and FastAPI.
                        Java knowledge is useful.
                        Kafka experience is a plus.
                        """,
                        List.of(
                                python,
                                java,
                                kafka
                        )
                );

        assertEquals(
                3,
                result.size()
        );

        var pythonResult =
                result.stream()
                        .filter(item ->
                                item.skill().getNormalizedName()
                                        .equals("python"))
                        .findFirst()
                        .orElseThrow();

        assertTrue(
                pythonResult.required()
        );

        var javaResult =
                result.stream()
                        .filter(item ->
                                item.skill().getNormalizedName()
                                        .equals("java"))
                        .findFirst()
                        .orElseThrow();

        assertFalse(
                javaResult.required()
        );
    }

    @Test
    void shouldReturnEmptyWhenNoKnownSkillsArePresent() {

        Skill java =
                mockSkill(
                        1L,
                        "Java",
                        "java"
                );

        List<SkillExtractor.ExtractedSkill> result =
                extractor.extract(
                        "Senior Professional",
                        "Work with business stakeholders.",
                        List.of(java)
                );

        assertTrue(
                result.isEmpty()
        );
    }

    private Skill mockSkill(
            Long id,
            String name,
            String normalizedName) {

        Skill skill =
                org.mockito.Mockito.mock(
                        Skill.class
                );

        org.mockito.Mockito.when(skill.getId())
                .thenReturn(id);

        org.mockito.Mockito.when(
                skill.getName()
        ).thenReturn(name);

        org.mockito.Mockito.when(
                skill.getNormalizedName()
        ).thenReturn(normalizedName);

        return skill;
    }
}