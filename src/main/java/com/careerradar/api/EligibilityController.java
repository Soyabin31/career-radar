package com.careerradar.api;

import com.careerradar.eligibility.EligibilityResult;
import com.careerradar.eligibility.EligibilityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    private final EligibilityService eligibilityService;

    public EligibilityController(
            EligibilityService eligibilityService) {

        this.eligibilityService =
                eligibilityService;
    }

    @GetMapping("/jobs/{jobId}")
    public EligibilityResponse evaluateJob(
            @PathVariable Long jobId) {

        EligibilityResult result =
                eligibilityService.evaluateJob(
                        jobId
                );

        return EligibilityResponse.from(
                result
        );
    }
}