package com.careerradar.ingestion;

import com.careerradar.company.CompanyRepository;
import com.careerradar.eligibility.LocationNormalizer;
import com.careerradar.job.JobRepository;
import com.careerradar.source.JobSourceEntity;
import com.careerradar.source.JobSourceRegistry;
import com.careerradar.source.JobSourceRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class JobIngestionServiceTest {

    @Test
    void shouldCreateService() {

        JobSourceRepository jobSourceRepository =
                mock(JobSourceRepository.class);

        JobSourceRegistry jobSourceRegistry =
                mock(JobSourceRegistry.class);

        LocationNormalizer locationNormalizer =
                mock(LocationNormalizer.class);

        JobRepository jobRepository =
                mock(JobRepository.class);

        CompanyRepository companyRepository =
                mock(CompanyRepository.class);

        JobIngestionService service =
                new JobIngestionService(
                        jobSourceRepository,
                        jobSourceRegistry,
                        jobRepository,
                        companyRepository,
                        locationNormalizer
                );

        org.junit.jupiter.api.Assertions.assertNotNull(service);
    }
}