package com.careerradar.api;

import com.careerradar.eligibility.EligibilityResult;
import com.careerradar.job.domain.EligibilityStatus;
import com.careerradar.matching.JobMatchResult;
import com.careerradar.opportunity.JobOpportunityResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JobOpportunityListResponseTest {

    @Test
    void shouldMapPostedAtAndDiscoveredAtInItem() {
        OffsetDateTime postedAt = OffsetDateTime.parse("2026-10-24T12:00:00Z");
        OffsetDateTime discoveredAt = OffsetDateTime.parse("2026-10-25T14:30:00Z");

        JobMatchResult match = new JobMatchResult(
                1L,
                "Senior Java Developer",
                "JAVA_BACKEND",
                new BigDecimal("90.00"),
                true,
                List.of("Java", "Spring Boot"),
                List.of("Kafka"),
                List.of()
        );

        EligibilityResult eligibility = new EligibilityResult(
                1L,
                EligibilityStatus.REGION_OK,
                true,
                true,
                true,
                List.of()
        );

        JobOpportunityResult result = new JobOpportunityResult(
                1L,
                "Senior Java Developer",
                "Miratech",
                "Description",
                "https://example.com/job/1",
                "Bengaluru, India",
                "India",
                "Bengaluru",
                "INDIA",
                null,
                "REMOTE",
                "FULL_TIME",
                "JAVA_BACKEND",
                postedAt,
                discoveredAt,
                match,
                eligibility
        );

        JobOpportunityListResponse response = JobOpportunityListResponse.from(List.of(result));

        assertEquals(1, response.count());
        JobOpportunityListResponse.Item item = response.opportunities().get(0);
        assertEquals(1L, item.jobId());
        assertEquals("Miratech", item.companyName());
        assertEquals(postedAt, item.postedAt());
        assertEquals(discoveredAt, item.discoveredAt());
        assertEquals(List.of("Java", "Spring Boot"), item.matchedSkills());
        assertEquals(List.of("Kafka"), item.missingSkills());
    }
}
