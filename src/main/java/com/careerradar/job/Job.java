package com.careerradar.job;

import com.careerradar.job.domain.EmploymentType;
import com.careerradar.job.domain.ForeignRegion;
import com.careerradar.job.domain.GeographyType;
import com.careerradar.job.domain.WorkplaceType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "jobs",
        indexes = {
                @Index(name = "idx_jobs_company_id", columnList = "company_id"),
                @Index(name = "idx_jobs_source_id", columnList = "source_id"),
                @Index(name = "idx_jobs_geography_type", columnList = "geography_type"),
                @Index(name = "idx_jobs_workplace_type", columnList = "workplace_type"),
                @Index(name = "idx_jobs_employment_type", columnList = "employment_type"),
                @Index(name = "idx_jobs_posted_at", columnList = "posted_at"),
                @Index(name = "idx_jobs_active", columnList = "active")
        }
)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "source_job_id")
    private String sourceJobId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(
            name = "canonical_url",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String canonicalUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "geography_type")
    private GeographyType geographyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "foreign_region")
    private ForeignRegion foreignRegion;

    @Enumerated(EnumType.STRING)
    @Column(name = "workplace_type")
    private WorkplaceType workplaceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    @Column(length = 255)
    private String country;

    @Column(name = "state_or_region", length = 255)
    private String stateOrRegion;

    @Column(length = 255)
    private String city;

    @Column(name = "salary_min", precision = 15, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "salary_max", precision = 15, scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "salary_currency", length = 10)
    private String salaryCurrency;

    @Column(name = "posted_at")
    private OffsetDateTime postedAt;

    @Column(name = "discovered_at", nullable = false)
    private OffsetDateTime discoveredAt;

    @Column(name = "last_seen_at")
    private OffsetDateTime lastSeenAt;

    @Column(name = "content_hash", length = 128)
    private String contentHash;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "location")
    private String location;

    protected Job() {
    }

    public static Job create(
            Long companyId,
            Long sourceId,
            String sourceJobId,
            String title,
            String description,
            String canonicalUrl,
            String location,
            String country,
            String city,
            OffsetDateTime postedAt) {

        OffsetDateTime now = OffsetDateTime.now();

        Job job = new Job();

        job.companyId = companyId;
        job.sourceId = sourceId;
        job.sourceJobId = sourceJobId;
        job.title = title;
        job.description = description;
        job.canonicalUrl = canonicalUrl;
        job.location = location;
        job.country = truncate(country, 255);
        job.city = truncate(city, 255);
        job.postedAt = postedAt;

        job.discoveredAt = now;
        job.lastSeenAt = now;
        job.createdAt = now;
        job.updatedAt = now;
        job.active = true;
        job.contentHash = computeContentHash(title, description, location);

        return job;
    }

    public static String computeContentHash(String title, String description, String location) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String payload = (title != null ? title.trim() : "") + "||" +
                    (description != null ? description.trim() : "") + "||" +
                    (location != null ? location.trim() : "");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    public boolean updateContentIfChanged(String title, String description, String location) {
        String newHash = computeContentHash(title, description, location);
        if (!Objects.equals(this.contentHash, newHash)) {
            this.title = title;
            this.description = description;
            this.location = location;
            this.contentHash = newHash;
            this.active = true;
            this.lastSeenAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
            return true;
        }
        return false;
    }

    public void markClosed() {
        this.active = false;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markSeen() {
        this.lastSeenAt = OffsetDateTime.now();
        this.active = true;
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public String getSourceJobId() {
        return sourceJobId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public GeographyType getGeographyType() {
        return geographyType;
    }

    public ForeignRegion getForeignRegion() {
        return foreignRegion;
    }

    public WorkplaceType getWorkplaceType() {
        return workplaceType;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public String getCountry() {
        return country;
    }

    public String getStateOrRegion() {
        return stateOrRegion;
    }

    public String getCity() {
        return city;
    }

    public BigDecimal getSalaryMin() {
        return salaryMin;
    }

    public BigDecimal getSalaryMax() {
        return salaryMax;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public OffsetDateTime getPostedAt() {
        return postedAt;
    }

    public OffsetDateTime getDiscoveredAt() {
        return discoveredAt;
    }

    public OffsetDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public String getContentHash() {
        return contentHash;
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

    public void applyLocation(
            GeographyType geographyType,
            ForeignRegion foreignRegion,
            WorkplaceType workplaceType,
            String country,
            String city) {

        this.geographyType = geographyType;
        this.foreignRegion = foreignRegion;
        this.workplaceType = workplaceType;
        this.country = truncate(country, 255);
        this.city = truncate(city, 255);
        this.updatedAt = OffsetDateTime.now();
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void applyEmploymentType(
            EmploymentType employmentType) {

        this.employmentType = employmentType;
    }
}