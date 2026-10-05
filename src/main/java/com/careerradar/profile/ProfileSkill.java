package com.careerradar.profile;

import com.careerradar.matching.Skill;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "profile_skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_profile_skills",
                        columnNames = {"profile_id", "skill_id"}
                )
        }
)
public class ProfileSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "profile_id",
            nullable = false
    )
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "skill_id",
            nullable = false
    )
    private Skill skill;

    @Column(
            name = "skill_type",
            nullable = false,
            length = 30
    )
    private String skillType;

    @Column(
            nullable = false,
            precision = 6,
            scale = 3
    )
    private BigDecimal weight;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;

    protected ProfileSkill() {
    }

    public static ProfileSkill create(
            Profile profile,
            Skill skill,
            String skillType,
            BigDecimal weight) {

        ProfileSkill profileSkill =
                new ProfileSkill();

        profileSkill.profile = profile;
        profileSkill.skill = skill;
        profileSkill.skillType = skillType;
        profileSkill.weight = weight;
        profileSkill.active = true;
        profileSkill.createdAt =
                OffsetDateTime.now();

        return profileSkill;
    }

    public Long getId() {
        return id;
    }

    public Profile getProfile() {
        return profile;
    }

    public Skill getSkill() {
        return skill;
    }

    public String getSkillType() {
        return skillType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public Boolean getActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}