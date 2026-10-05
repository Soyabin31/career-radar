package com.careerradar.profile;

import com.careerradar.job.domain.WorkplaceType;
import jakarta.persistence.*;

@Entity
@Table(
        name = "profile_workplace_preferences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_profile_workplace",
                        columnNames = {
                                "profile_id",
                                "workplace_type"
                        }
                )
        }
)
public class ProfileWorkplacePreference {

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
            name = "workplace_type",
            nullable = false,
            length = 30
    )
    private WorkplaceType workplaceType;

    @Column(nullable = false)
    private Integer priority = 1;

    @Column(nullable = false)
    private Boolean active = true;

    protected ProfileWorkplacePreference() {
    }

    public Long getId() {
        return id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public WorkplaceType getWorkplaceType() {
        return workplaceType;
    }

    public Integer getPriority() {
        return priority;
    }

    public Boolean getActive() {
        return active;
    }
}