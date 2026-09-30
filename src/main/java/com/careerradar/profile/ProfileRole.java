package com.careerradar.profile;

import com.careerradar.matching.RoleFamily;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "profile_roles")
public class ProfileRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "role_name", nullable = false, length = 200)
    private String roleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_family", nullable = false, length = 50)
    private RoleFamily roleFamily;

    @Column(nullable = false)
    private Integer priority = 1;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected ProfileRole() {
    }

    public Long getId() {
        return id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public String getRoleName() {
        return roleName;
    }

    public RoleFamily getRoleFamily() {
        return roleFamily;
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