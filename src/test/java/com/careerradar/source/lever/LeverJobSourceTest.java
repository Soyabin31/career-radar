package com.careerradar.source.lever;

import com.careerradar.source.JobSourceEntity;
import com.careerradar.source.RawJob;
import com.careerradar.source.SourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class LeverJobSourceTest {

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer mockServer;
    private LeverJobSource leverJobSource;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        leverJobSource = new LeverJobSource(restClientBuilder);
    }

    @Test
    void shouldReturnLeverSourceType() {
        assertEquals(SourceType.LEVER, leverJobSource.type());
    }

    @Test
    void shouldFetchAndMapLeverJobs() {
        String json = """
            [
              {
                "id": "lever-123",
                "text": "Senior Software Engineer",
                "createdAt": 1700000000000,
                "descriptionPlain": "Build backend microservices with Java.",
                "hostedUrl": "https://jobs.lever.co/techcorp/lever-123",
                "categories": {
                  "commitment": "Full time",
                  "location": "San Francisco, CA, USA",
                  "team": "Platform"
                }
              }
            ]
            """;

        mockServer.expect(requestTo("https://api.lever.co/v0/postings/techcorp?mode=json"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        JobSourceEntity entity = JobSourceEntity.create(
                1L,
                SourceType.LEVER,
                "techcorp",
                "https://jobs.lever.co/techcorp"
        );

        List<RawJob> jobs = leverJobSource.fetch(entity);

        mockServer.verify();
        assertNotNull(jobs);
        assertEquals(1, jobs.size());

        RawJob job = jobs.get(0);
        assertEquals("lever-123", job.sourceJobId());
        assertEquals("Senior Software Engineer", job.title());
        assertEquals("Build backend microservices with Java.", job.description());
        assertEquals("techcorp", job.companyName());
        assertEquals("San Francisco, CA, USA", job.location());
        assertEquals("USA", job.country());
        assertEquals("San Francisco", job.city());
        assertEquals("https://jobs.lever.co/techcorp/lever-123", job.url());
        assertNotNull(job.postedAt());
    }

    @Test
    void shouldThrowExceptionWhenSourceIdentifierIsMissing() {
        JobSourceEntity entity = JobSourceEntity.create(
                1L,
                SourceType.LEVER,
                null,
                "https://jobs.lever.co"
        );
        assertThrows(IllegalArgumentException.class, () -> leverJobSource.fetch(entity));
    }
}
