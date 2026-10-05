package com.careerradar.api;

import com.careerradar.job.domain.EligibilityStatus;
import com.careerradar.job.domain.EmploymentType;
import com.careerradar.job.domain.GeographyType;
import com.careerradar.job.domain.RoleFamily;
import com.careerradar.job.domain.WorkplaceType;
import com.careerradar.opportunity.JobOpportunityFeedService;
import com.careerradar.opportunity.JobOpportunityResult;
import com.careerradar.opportunity.OpportunityFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class JobOpportunityFeedController {

    private final JobOpportunityFeedService feedService;

    public JobOpportunityFeedController(
            JobOpportunityFeedService feedService) {

        this.feedService = feedService;
    }

    @GetMapping
    public JobOpportunityListResponse getOpportunities(
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false)
            EligibilityStatus eligibility,
            @RequestParam(required = false)
            GeographyType geography,
            @RequestParam(required = false)
            WorkplaceType workplace,
            @RequestParam(required = false)
            EmploymentType employment,
            @RequestParam(required = false)
            RoleFamily roleFamily,
            @RequestParam(required = false)
            Integer postedWithinDays) {

        OpportunityFilter filter =
                new OpportunityFilter(
                        minScore,
                        eligibility,
                        geography,
                        workplace,
                        employment,
                        roleFamily,
                        postedWithinDays
                );

        List<JobOpportunityResult> results =
                feedService.findOpportunities(filter);

        return JobOpportunityListResponse.from(results);
    }
}