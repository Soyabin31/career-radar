package com.careerradar.profile;

import com.careerradar.job.domain.ForeignRegion;
import com.careerradar.job.domain.GeographyType;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "profile_locations")
public class ProfileLocation {

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
            name = "geography_type",
            nullable = false,
            length = 30
    )
    private GeographyType geographyType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "foreign_region",
            length = 30
    )
    private ForeignRegion foreignRegion;

    @Column(length = 100)
    private String country;

    @Column(
            name = "state_or_region",
            length = 100
    )
    private String stateOrRegion;

    @Column(length = 150)
    private String city;

    @Column(nullable = false)
    private Integer priority = 1;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;

    protected ProfileLocation() {
    }

    public Long getId() {
        return id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public GeographyType getGeographyType() {
        return geographyType;
    }

    public ForeignRegion getForeignRegion() {
        return foreignRegion;
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

    public Integer getPriority() {
        return priority;
    }

    public Boolean getActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}