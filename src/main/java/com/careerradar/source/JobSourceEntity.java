package com.careerradar.source;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "job_sources")
public class JobSourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id")
    private Long companyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "source_identifier")
    private String sourceIdentifier;

    @Column(name = "source_url", nullable = false)
    private String sourceUrl;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected JobSourceEntity() {
    }

    public static JobSourceEntity create(
            Long companyId,
            SourceType sourceType,
            String sourceIdentifier,
            String sourceUrl) {

        OffsetDateTime now = OffsetDateTime.now();

        JobSourceEntity entity = new JobSourceEntity();
        entity.companyId = companyId;
        entity.sourceType = sourceType;
        entity.sourceIdentifier = sourceIdentifier;
        entity.sourceUrl = sourceUrl != null ? sourceUrl : "";
        entity.active = true;
        entity.createdAt = now;
        entity.updatedAt = now;

        return entity;
    }

    public Long getId() {
        return id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public Boolean getActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}