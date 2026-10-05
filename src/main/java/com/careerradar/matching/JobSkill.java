package com.careerradar.matching;

import com.careerradar.job.Job;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "job_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_skills",
                        columnNames = {"job_id", "skill_id"}
                )
        }
)
public class JobSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "job_id",
            nullable = false
    )
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "skill_id",
            nullable = false
    )
    private Skill skill;

    @Column(
            nullable = false
    )
    private Boolean required = true;

    @Column(
            nullable = false,
            precision = 5,
            scale = 4
    )
    private BigDecimal confidence;

    @Column(length = 50)
    private String source;

    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;

    protected JobSkill() {
    }

    public static JobSkill create(
            Job job,
            Skill skill,
            boolean required,
            double confidence,
            String source) {

        JobSkill jobSkill = new JobSkill();

        jobSkill.job = job;
        jobSkill.skill = skill;
        jobSkill.required = required;
        jobSkill.confidence =
                BigDecimal.valueOf(confidence);
        jobSkill.source = source;
        jobSkill.createdAt =
                OffsetDateTime.now();

        return jobSkill;
    }

    public void update(
            boolean required,
            double confidence,
            String source) {

        this.required = required;
        this.confidence =
                BigDecimal.valueOf(confidence);
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public Job getJob() {
        return job;
    }

    public Skill getSkill() {
        return skill;
    }

    public Boolean getRequired() {
        return required;
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