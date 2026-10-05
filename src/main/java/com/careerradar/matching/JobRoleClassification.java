package com.careerradar.matching;

import com.careerradar.job.Job;
import com.careerradar.job.domain.RoleFamily;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "job_role_classifications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_role_classification",
                        columnNames = {"job_id", "role_family"}
                )
        }
)
public class JobRoleClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "job_id",
            nullable = false
    )
    private Job job;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role_family",
            nullable = false,
            length = 50
    )
    private RoleFamily roleFamily;

    @Column(
            nullable = false,
            precision = 5,
            scale = 4
    )
    private BigDecimal confidence;

    @Column(
            nullable = false,
            length = 50
    )
    private String source;

    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;

    protected JobRoleClassification() {
    }

    public static JobRoleClassification create(
            Job job,
            RoleFamily roleFamily,
            double confidence,
            String source) {

        JobRoleClassification classification =
                new JobRoleClassification();

        classification.job = job;
        classification.roleFamily = roleFamily;
        classification.confidence =
                BigDecimal.valueOf(confidence);
        classification.source = source;
        classification.createdAt =
                OffsetDateTime.now();

        return classification;
    }

    public void update(
            BigDecimal confidence,
            String source) {

        this.confidence = confidence;

        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public Job getJob() {
        return job;
    }

    public RoleFamily getRoleFamily() {
        return roleFamily;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public String getSource() {
        return source;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}