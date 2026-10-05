package com.careerradar.profile;

import com.careerradar.job.domain.EmploymentType;
import jakarta.persistence.*;

@Entity
@Table(
        name = "profile_employment_preferences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_profile_employment",
                        columnNames = {
                                "profile_id",
                                "employment_type"
                        }
                )
        }
)
public class ProfileEmploymentPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "profile_id",
            nullable = false
    )
    private Long profileId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "employment_type",
            nullable = false,
            length = 30
    )
    private EmploymentType employmentType;

    @Column(nullable = false)
    private Integer priority = 1;

    @Column(nullable = false)
    private Boolean active = true;

    protected ProfileEmploymentPreference() {
    }

    public Long getId() {
        return id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public Integer getPriority() {
        return priority;
    }

    public Boolean getActive() {
        return active;
    }
}