package com.careerradar.api;

import com.careerradar.matching.JobRoleClassification;
import com.careerradar.matching.RoleClassificationResponse;
import com.careerradar.matching.RoleClassificationResult;
import com.careerradar.matching.RoleClassificationService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/role-classifications")
public class RoleClassificationController {

    private final RoleClassificationService classificationService;

    public RoleClassificationController(
            RoleClassificationService classificationService) {

        this.classificationService = classificationService;
    }

    @PostMapping("/classify")
    public ClassifyAllResponse classifyAll() {

        int classified =
                classificationService.classifyAllJobs();

        return new ClassifyAllResponse(classified);
    }

    @PostMapping("/jobs/{jobId}/classify")
    public RoleClassificationResponse classifyJob(
            @PathVariable Long jobId) {

        RoleClassificationResult result =
                classificationService.classifyJob(jobId);

        return new RoleClassificationResponse(
                jobId,
                result.roleFamily().name(),
                BigDecimal.valueOf(result.confidence()),
                "RULE_BASED"
        );
    }

    @GetMapping("/jobs/{jobId}")
    public List<RoleClassificationResponse> getClassifications(
            @PathVariable Long jobId) {

        List<JobRoleClassification> classifications =
                classificationService.getClassifications(jobId);

        return classifications
                .stream()
                .map(RoleClassificationResponse::from)
                .toList();
    }

    public record ClassifyAllResponse(
            int classified
    ) {
    }
}