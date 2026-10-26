package com.careerradar.source.workable;

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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WorkableJobSourceTest {

    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer mockServer;
    private WorkableJobSource workableJobSource;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        workableJobSource = new WorkableJobSource(restClientBuilder);
    }

    @Test
    void shouldReturnWorkableSourceType() {
        assertEquals(SourceType.WORKABLE, workableJobSource.type());
    }

    @Test
    void shouldFetchAndMapWorkableJobs() {
        String json = """
            {
              "name": "Hugging Face",
              "jobs": [
                {
                  "id": "hf-201",
                  "title": "Staff Backend Engineer - Machine Learning Systems",
                  "department": "Engineering",
                  "workplace_type": "remote",
                  "url": "https://apply.workable.com/huggingface/j/hf-201/",
                  "published_on": "2026-10-20",
                  "description": "Design distributed scalable ML serving backends.",
                  "location": {
                    "location_str": "Remote, Global",
                    "country": "Remote",
                    "city": "Global"
                  }
                }
              ]
            }
            """;

        mockServer.expect(requestTo("https://apply.workable.com/api/v1/widget/accounts/huggingface?details=true"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) CareerRadar/2.0"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        JobSourceEntity entity = JobSourceEntity.create(
                1L,
                SourceType.WORKABLE,
                "huggingface",
                "https://apply.workable.com/huggingface"
        );
        ReflectionTestUtils.setField(entity, "id", 15L);

        List<RawJob> jobs = workableJobSource.fetch(entity);

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        RawJob job = jobs.get(0);
        assertEquals("hf-201", job.sourceJobId());
        assertEquals("Staff Backend Engineer - Machine Learning Systems", job.title());
        assertEquals("Hugging Face", job.companyName());
        assertEquals("Remote, Global", job.location());
        assertEquals("https://apply.workable.com/huggingface/j/hf-201/", job.url());
        assertNotNull(job.postedAt());
    }
}
