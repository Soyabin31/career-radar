package com.careerradar.api;

import com.careerradar.matching.JobSkill;
import com.careerradar.matching.JobSkillExtractionService;
import com.careerradar.matching.JobSkillQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-skills")
public class JobSkillController {

    private final JobSkillExtractionService extractionService;
    private final JobSkillQueryService queryService;

    public JobSkillController(
            JobSkillExtractionService extractionService,
            JobSkillQueryService queryService) {

        this.extractionService =
                extractionService;

        this.queryService =
                queryService;
    }

    @PostMapping("/extract")
    public ExtractAllResponse extractAll() {

        int extracted =
                extractionService.extractAllJobs();

        return new ExtractAllResponse(extracted);
    }

    @PostMapping("/jobs/{jobId}/extract")
    public ExtractJobResponse extractJob(
            @PathVariable Long jobId) {

        int extracted =
                extractionService.extractJob(jobId);

        return new ExtractJobResponse(
                jobId,
                extracted
        );
    }

    @GetMapping("/jobs/{jobId}")
    public List<JobSkillResponse> getJobSkills(
            @PathVariable Long jobId) {

        List<JobSkill> skills =
                queryService.getSkillsForJob(jobId);

        return skills.stream()
                .map(JobSkillResponse::from)
                .toList();
    }

    public record ExtractAllResponse(
            int extracted
    ) {
    }

    public record ExtractJobResponse(
            Long jobId,
            int extracted
    ) {
    }
}