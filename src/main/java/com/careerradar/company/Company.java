package com.careerradar.company;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200, unique = true)
    private String name;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "careers_url")
    private String careersUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", nullable = false)
    private CompanyType companyType;

    @Column(name = "headquarters_country")
    private String headquartersCountry;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Company() {
    }

    public static Company create(
            String name,
            String websiteUrl,
            String careersUrl,
            CompanyType companyType,
            String headquartersCountry) {

        OffsetDateTime now = OffsetDateTime.now();

        Company company = new Company();

        company.name = name;
        company.websiteUrl = websiteUrl;
        company.careersUrl = careersUrl;
        company.companyType =
                companyType != null
                        ? companyType
                        : CompanyType.OTHER;
        company.headquartersCountry = headquartersCountry;

        company.active = true;
        company.createdAt = now;
        company.updatedAt = now;

        return company;
    }

    public void updateDetails(
            String websiteUrl,
            String careersUrl,
            CompanyType companyType,
            String headquartersCountry) {

        this.websiteUrl = websiteUrl;
        this.careersUrl = careersUrl;

        if (companyType != null) {
            this.companyType = companyType;
        }

        this.headquartersCountry = headquartersCountry;
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getCareersUrl() {
        return careersUrl;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public String getHeadquartersCountry() {
        return headquartersCountry;
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
}