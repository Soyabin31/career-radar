package com.careerradar.profile;

import com.careerradar.matching.Skill;
import com.careerradar.matching.SkillType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "profile_skills")
public class ProfileSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false, length = 30)
    private SkillType skillType;

    @Column(nullable = false, precision = 6, scale = 3)
    private BigDecimal weight = BigDecimal.ONE;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected ProfileSkill() {
    }

    public Long getId() {
        return id;
    }

    public Long getProfileId() {
        return profileId;
    }

    public Skill getSkill() {
        return skill;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public Boolean getActive() {
        return active;
    }
}