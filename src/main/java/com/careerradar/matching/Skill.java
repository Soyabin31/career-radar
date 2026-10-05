package com.careerradar.matching;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "skills",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_skills_normalized_name",
                        columnNames = "normalized_name"
                )
        }
)
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            name = "normalized_name",
            nullable = false,
            length = 150,
            unique = true
    )
    private String normalizedName;

    @Column(length = 100)
    private String category;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(
            name = "created_at",
            nullable = false
    )
    private OffsetDateTime createdAt;

    protected Skill() {
    }

    public static Skill create(
            String name,
            String normalizedName,
            String category) {

        Skill skill = new Skill();

        skill.name = name;
        skill.normalizedName = normalizedName;
        skill.category = category;
        skill.active = true;
        skill.createdAt = OffsetDateTime.now();

        return skill;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public String getCategory() {
        return category;
    }

    public Boolean getActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}