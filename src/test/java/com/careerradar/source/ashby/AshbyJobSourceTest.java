package com.careerradar.source.ashby;

import com.careerradar.source.JobSourceEntity;
import com.careerradar.source.RawJob;
import com.careerradar.source.SourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AshbyJobSourceTest {

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer mockServer;
    private AshbyJobSource ashbyJobSource;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        ashbyJobSource = new AshbyJobSource(restClientBuilder);
    }

    @Test
    void shouldReturnAshbySourceType() {
        assertEquals(SourceType.ASHBY, ashbyJobSource.type());
    }

    @Test
    void shouldFetchAndMapAshbyJobs() {
        String json = """
            {
              "apiVersion": "0.1",
              "jobs": [
                {
                  "id": "ashby-456",
                  "title": "Lead Infrastructure Engineer",
                  "location": "New York, NY, United States",
                  "isRemote": true,
                  "jobUrl": "https://jobs.ashbyhq.com/innovate/ashby-456",
                  "publishedAt": "2026-03-15T10:00:00.000Z",
                  "descriptionPlain": "Lead cloud platform engineering."
                }
              ]
            }
            """;

        mockServer.expect(requestTo("https://api.ashbyhq.com/posting-api/job-board/innovate"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        JobSourceEntity entity = JobSourceEntity.create(
                1L,
                SourceType.ASHBY,
                "innovate",
                "https://jobs.ashbyhq.com/innovate"
        );

        List<RawJob> jobs = ashbyJobSource.fetch(entity);

        mockServer.verify();
        assertNotNull(jobs);
        assertEquals(1, jobs.size());

        RawJob job = jobs.get(0);
        assertEquals("ashby-456", job.sourceJobId());
        assertEquals("Lead Infrastructure Engineer", job.title());
        assertEquals("Lead cloud platform engineering.", job.description());
        assertEquals("innovate", job.companyName());
        assertEquals("New York, NY, United States", job.location());
        assertEquals("United States", job.country());
        assertEquals("New York", job.city());
        assertEquals("https://jobs.ashbyhq.com/innovate/ashby-456", job.url());
        assertNotNull(job.postedAt());
    }

    @Test
    void shouldThrowExceptionWhenSourceIdentifierIsMissing() {
        JobSourceEntity entity = JobSourceEntity.create(
                1L,
                SourceType.ASHBY,
                null,
                "https://jobs.ashbyhq.com"
        );
        assertThrows(IllegalArgumentException.class, () -> ashbyJobSource.fetch(entity));
    }
}
