package com.careerradar.api;

import com.careerradar.opportunity.JobOpportunityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/opportunities")
public class JobOpportunityController {

    private final JobOpportunityService jobOpportunityService;

    public JobOpportunityController(
            JobOpportunityService jobOpportunityService) {

        this.jobOpportunityService = jobOpportunityService;
    }

    @GetMapping("/jobs/{jobId}")
    public JobOpportunityResponse getOpportunity(
            @PathVariable Long jobId) {

        return JobOpportunityResponse.from(
                jobOpportunityService.getOpportunity(jobId)
        );
    }
}