package com.careerradar.api;

import com.careerradar.ingestion.JobIngestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {

    private final JobIngestionService jobIngestionService;

    public IngestionController(JobIngestionService jobIngestionService) {
        this.jobIngestionService = jobIngestionService;
    }

    @PostMapping("/scan")
    public ScanResponse scan() {

        int inserted =
                jobIngestionService.ingestAllActiveSources();

        return new ScanResponse(inserted);
    }

    public record ScanResponse(int inserted) {
    }
}