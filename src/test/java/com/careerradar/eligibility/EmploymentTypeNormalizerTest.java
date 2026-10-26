package com.careerradar.eligibility;

import com.careerradar.job.domain.EmploymentType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmploymentTypeNormalizerTest {

    private final EmploymentTypeNormalizer normalizer =
            new EmploymentTypeNormalizer();

    @Test
    void shouldDetectFullTime() {

        assertThat(
                normalizer.normalize(
                        "Senior Java Backend Engineer",
                        "This is a full-time permanent position."
                )
        )
                .isEqualTo(EmploymentType.FULL_TIME);
    }

    @Test
    void shouldDetectContract() {

        assertThat(
                normalizer.normalize(
                        "Senior Backend Engineer",
                        "12 month contract position."
                )
        )
                .isEqualTo(EmploymentType.CONTRACT);
    }

    @Test
    void shouldDetectFreelance() {

        assertThat(
                normalizer.normalize(
                        "Java Backend Engineer",
                        "Freelance opportunity."
                )
        )
                .isEqualTo(EmploymentType.FREELANCE);
    }

    @Test
    void shouldDetectConsulting() {

        assertThat(
                normalizer.normalize(
                        "Technology Consultant",
                        "Consulting engagement."
                )
        )
                .isEqualTo(EmploymentType.CONSULTING);
    }

    @Test
    void shouldDetectPartTime() {

        assertThat(
                normalizer.normalize(
                        "Backend Engineer",
                        "This is a part-time position."
                )
        )
                .isEqualTo(EmploymentType.PART_TIME);
    }

    @Test
    void shouldDetectProject() {

        assertThat(
                normalizer.normalize(
                        "Java Engineer",
                        "Project-based engagement."
                )
        )
                .isEqualTo(EmploymentType.PROJECT);
    }

    @Test
    void shouldReturnUnknownWhenEmploymentTypeCannotBeDetermined() {

        assertThat(
                normalizer.normalize(
                        "Senior Java Backend Engineer",
                        "Build Spring Boot microservices."
                )
        )
                .isNull();
    }

    @Test
    void shouldHandleNullInput() {

        assertThat(
                normalizer.normalize(null, null)
        )
                .isNull();
    }
}