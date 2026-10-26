package com.careerradar.source.smartrecruiters;

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

class SmartRecruitersJobSourceTest {

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer mockServer;
    private SmartRecruitersJobSource smartRecruitersJobSource;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        smartRecruitersJobSource = new SmartRecruitersJobSource(restClientBuilder);
    }

    @Test
    void shouldReturnSmartRecruitersSourceType() {
        assertEquals(SourceType.SMARTRECRUITERS, smartRecruitersJobSource.type());
    }

    @Test
    void shouldFetchAndMapSmartRecruitersJobs() {
        String json = """
            {
              "totalFound": 1,
              "content": [
                {
                  "id": "sr-101",
                  "name": "Senior Backend Engineer - Java",
                  "releasedDate": "2026-10-24T12:00:00Z",
                  "location": {
                    "city": "Bengaluru",
                    "region": "KA",
                    "country": "in",
                    "remote": true,
                    "fullLocation": "Bengaluru, Karnataka, India"
                  },
                  "company": {
                    "identifier": "visa",
                    "name": "Visa"
                  }
                }
              ]
            }
            """;

        mockServer.expect(requestTo("https://api.smartrecruiters.com/v1/companies/visa/postings?limit=100"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        JobSourceEntity entity = JobSourceEntity.create(
                1L,
                SourceType.SMARTRECRUITERS,
                "visa",
                "https://jobs.smartrecruiters.com/visa"
        );
        ReflectionTestUtils.setField(entity, "id", 10L);

        List<RawJob> jobs = smartRecruitersJobSource.fetch(entity);

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        RawJob job = jobs.get(0);
        assertEquals("sr-101", job.sourceJobId());
        assertEquals("Senior Backend Engineer - Java", job.title());
        assertEquals("Visa", job.companyName());
        assertEquals("Bengaluru, Karnataka, India", job.location());
        assertEquals("in", job.country());
        assertEquals("Bengaluru", job.city());
        assertEquals("https://jobs.smartrecruiters.com/visa/sr-101", job.url());
        assertNotNull(job.postedAt());
    }
}
