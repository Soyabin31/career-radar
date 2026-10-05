package com.careerradar.api;

import com.careerradar.matching.JobMatchResult;
import com.careerradar.matching.JobMatchingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matching")
public class JobMatchingController {

    private final JobMatchingService matchingService;

    public JobMatchingController(
            JobMatchingService matchingService) {

        this.matchingService =
                matchingService;
    }

    @GetMapping("/jobs/{jobId}")
    public JobMatchResponse matchJob(
            @PathVariable Long jobId) {

        JobMatchResult result =
                matchingService.matchJob(jobId);

        return JobMatchResponse.from(result);
    }
}