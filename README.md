# Career Radar

**Career Radar** is a free and extensible job-opportunity monitoring platform designed to discover, normalize, filter, classify, match, and track software-engineering opportunities from multiple job sources.

It is designed primarily for individual job seekers who want a single system for monitoring company career portals, applicant-tracking systems, remote-job platforms, community sources, and other legitimate job feeds.

Career Radar focuses on **discovery and decision support**. It does not submit job applications automatically.

---

## Features

Career Radar is designed around the following capabilities:

* Multi-source job discovery
* Direct company career and ATS integration
* Remote-job and job-board integration
* Company and source management
* Job deduplication
* Location normalization
* Geography classification
* Workplace classification
* Employment-type classification
* Role-family classification
* Job-skill extraction
* Candidate-profile configuration
* Profile-specific skill weighting
* Eligibility evaluation
* Explainable job/profile matching
* Job opportunity filtering
* Notification channels
* Application tracking
* Source health monitoring
* Multiple candidate profiles
* Extensible source architecture
* Local-first development
* PostgreSQL persistence
* Docker-based database setup
* REST API
* Scheduled and manually triggered ingestion

The architecture is intentionally modular so that new job sources, matching strategies, notification channels, and profile capabilities can be added without rewriting the core application.

---

# Architecture

Career Radar is implemented as a **modular monolith**.

The application uses clear domain boundaries rather than prematurely splitting functionality into microservices.

```text
                         ┌───────────────────────┐
                         │      Job Sources      │
                         │                       │
                         │ Greenhouse            │
                         │ Workday               │
                         │ Lever                 │
                         │ Ashby                 │
                         │ Workable              │
                         │ SmartRecruiters       │
                         │ Recruitee             │
                         │ Remote platforms      │
                         │ Community sources     │
                         └───────────┬───────────┘
                                     │
                                     ▼
                         ┌───────────────────────┐
                         │      Ingestion        │
                         │                       │
                         │ Fetch                 │
                         │ Validate              │
                         │ Normalize             │
                         │ Deduplicate           │
                         │ Persist               │
                         └───────────┬───────────┘
                                     │
                                     ▼
              ┌──────────────────────────────────────────┐
              │             Job Intelligence             │
              │                                          │
              │ Location normalization                    │
              │ Role classification                       │
              │ Skill extraction                          │
              │ Eligibility evaluation                    │
              │ Profile matching                          │
              │ Explainable scoring                       │
              └─────────────────────┬────────────────────┘
                                    │
                                    ▼
                    ┌─────────────────────────────┐
                    │       Opportunity Layer     │
                    │                             │
                    │ Matching jobs               │
                    │ Filters                     │
                    │ Notifications               │
                    │ Shortlisting                │
                    │ Application tracking        │
                    └─────────────────────────────┘
                                    │
                                    ▼
                         ┌───────────────────────┐
                         │     PostgreSQL        │
                         └───────────────────────┘
```

---

# Technology Stack

## Backend

* Java 23
* Spring Boot 3.5
* Spring Web
* Spring Data JPA
* Jakarta Persistence
* Bean Validation

## Database

* PostgreSQL 17
* Flyway database migrations

## HTTP

* Spring `RestClient`

## Testing

* JUnit
* Spring Boot Test
* Testcontainers
* PostgreSQL integration testing

## Runtime / Infrastructure

* Docker
* Docker Compose
* Maven

## Development Environment

Career Radar is developed and tested primarily on:

* Windows 11
* IntelliJ IDEA
* Amazon Corretto JDK
* Docker Desktop
* WSL2

The application is designed so that the database can run independently in Docker while the Spring Boot application runs locally.

---

# Project Structure

```text
career-radar/
│
├── pom.xml
├── README.md
├── docker-compose.yml
├── .gitignore
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── careerradar/
│   │   │           │
│   │   │           ├── CareerRadarApplication.java
│   │   │           │
│   │   │           ├── api/
│   │   │           │
│   │   │           ├── company/
│   │   │           │
│   │   │           ├── config/
│   │   │           │
│   │   │           ├── eligibility/
│   │   │           │
│   │   │           ├── ingestion/
│   │   │           │
│   │   │           ├── job/
│   │   │           │
│   │   │           ├── matching/
│   │   │           │
│   │   │           ├── notification/
│   │   │           │
│   │   │           ├── profile/
│   │   │           │
│   │   │           └── source/
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── careerradar/
```

The package structure follows business responsibilities rather than technical framework layers.

---

# Source Architecture

Job ingestion is based on a provider abstraction.

```java
public interface JobSource {

    SourceType type();

    List<RawJob> fetch(JobSourceEntity configuration);
}
```

Each provider is responsible for translating its external representation into the common `RawJob` model.

This prevents source-specific formats from leaking into the rest of the application.

A central registry resolves the correct implementation:

```text
SourceType
    │
    ▼
JobSourceRegistry
    │
    ├── GREENHOUSE
    ├── WORKDAY
    ├── LEVER
    ├── ASHBY
    ├── WORKABLE
    ├── SMARTRECRUITERS
    ├── RECRUITEE
    ├── REMOTE_OK
    ├── REMOTIVE
    ├── WE_WORK_REMOTELY
    ├── HACKER_NEWS
    └── ...
```

Adding a source should normally require:

1. A source implementation
2. Source-specific response models
3. Source configuration
4. Tests

The ingestion pipeline itself should remain unchanged.

---

# Supported Source Categories

Career Radar supports an extensible set of source types.

## ATS / Recruiting Platforms

* Greenhouse
* Workday
* Lever
* Ashby
* Workable
* SmartRecruiters
* Recruitee

## Job Platforms

* Remote OK
* Remotive
* We Work Remotely
* Naukri
* Instahyre
* Indeed

Availability and integration method depend on whether a source provides a legitimate public API, feed, or other permitted access mechanism.

## Community Sources

* Hacker News "Who is Hiring"

## Company Career Pages

Individual company career portals can be supported through dedicated source implementations where appropriate.

## Freelance Sources

Freelance opportunities are represented separately from traditional full-time employment so that employment type remains explicit.

---

# Job Ingestion Pipeline

A typical job passes through the following pipeline:

```text
External Source
      │
      ▼
RawJob
      │
      ▼
Validation
      │
      ▼
Company Resolution
      │
      ▼
Location Normalization
      │
      ▼
Deduplication
      │
      ▼
Job Persistence
      │
      ▼
Role Classification
      │
      ▼
Skill Extraction
      │
      ▼
Eligibility
      │
      ▼
Profile Matching
      │
      ▼
Notification / Application Tracking
```

The stages are deliberately separated so that ingestion remains deterministic and testable.

---

# Company Management

Companies are first-class domain entities.

A company can contain:

* Name
* Website
* Careers URL
* Company type
* Headquarters country
* Active status
* Associated job sources

Supported company classifications include:

```text
PRODUCT
SERVICE
CONSULTING
STARTUP
NON_PROFIT
GOVERNMENT
OTHER
```

Company classification is preference data and is not used as a hard-coded exclusion rule.

---

# Job Domain

A job contains source-independent information such as:

* Title
* Description
* Company
* Source
* Source job ID
* Canonical URL
* Geography
* Foreign region
* Workplace type
* Employment type
* Country
* State / region
* City
* Salary information
* Posted timestamp
* Discovery timestamp
* Last-seen timestamp
* Content hash
* Active status

The original job URL is retained so that the user can navigate to the source and apply there.

Career Radar does not replace the original application system.

---

# Geography

Geography is modeled independently from workplace type.

Supported high-level geography categories are:

```text
INDIA
FOREIGN
```

Indian locations can include cities such as:

* Bengaluru
* Pune
* Kolkata
* Bhubaneswar

Foreign opportunities can be grouped into:

```text
APAC
EUROPE
UK
USA
OTHER
```

This separation allows a foreign remote opportunity and a foreign onsite opportunity to be evaluated differently.

---

# Workplace Type

Workplace classification is independent of geography.

Supported values:

```text
REMOTE
HYBRID
ONSITE
UNKNOWN
```

For example:

```text
Remote + India
Hybrid + Bengaluru
Onsite + Germany
Remote + Europe
Onsite + USA
```

This prevents geography and workplace information from being conflated.

Unknown information is preserved as `UNKNOWN` rather than being guessed.

---

# Employment Type

Employment type is modeled independently from workplace and geography.

Supported values:

```text
FULL_TIME
PART_TIME
CONTRACT
FREELANCE
CONSULTING
PROJECT
```

This allows the system to distinguish, for example:

```text
Full-time remote
Contract remote
Freelance remote
Full-time onsite
Consulting hybrid
```

---

# Candidate Profiles

Candidate preferences are represented using profile entities rather than hard-coded application logic.

A profile can contain:

* Target roles
* Role families
* Skills
* Skill classifications
* Skill weights
* Locations
* Workplace preferences
* Employment preferences
* Company preferences

This makes the system capable of supporting multiple profiles in the future.

---

# Role Families

Career Radar classifies jobs into role families such as:

```text
JAVA_BACKEND
JAVA_PLATFORM
SOFTWARE_ENGINEER
BACKEND_ENGINEER
PYTHON_BACKEND
PYTHON_DATA
DATA_ENGINEER
DATA_SCIENCE
MACHINE_LEARNING
DEVOPS
SRE
CLOUD_ENGINEER
FRONTEND
MOBILE
QA
SECURITY
UNKNOWN
```

Role classification is intentionally performed before technology matching.

This is important because a job mentioning Kubernetes, Kafka, or GCP is not automatically a Java backend opportunity.

For example:

```text
Python Backend Engineer
+ Kubernetes
+ Kafka
+ GCP
```

should remain primarily a Python backend role.

Whereas:

```text
Senior Java Backend Engineer
+ Spring Boot
+ Kubernetes
+ Kafka
+ GCP
```

is naturally aligned with a Java backend role.

---

# Skill Model

Skills are stored independently from profiles and jobs.

A profile can classify skills as:

```text
PRIMARY
SECONDARY
SUPPORTING
```

This allows the same skill to have different importance depending on the candidate profile.

For example, a Java-focused backend profile may consider:

```text
Java             PRIMARY
Spring Boot      PRIMARY
Spring Security  PRIMARY
Kafka             PRIMARY
Kubernetes        PRIMARY
GCP               PRIMARY
Python            SECONDARY
Pandas            SECONDARY
FastAPI           SECONDARY
```

Skill weights allow matching to prioritize important skills without treating every keyword equally.

---

# Job Skills

A job can contain extracted skills with:

* Skill
* Required / optional indication
* Confidence
* Extraction source

A keyword appearing in a job description does not automatically mean that the technology is a required qualification.

This distinction is important for reducing keyword-driven false positives.

---

# Matching

Matching is designed around several independent dimensions.

```text
                  Job
                   │
        ┌──────────┼───────────┐
        ▼          ▼           ▼
   Role Fit    Skill Fit   Eligibility
        │          │           │
        └──────────┼───────────┘
                   ▼
          Opportunity Match
```

The system considers:

1. Role family
2. Target role
3. Primary skills
4. Secondary skills
5. Supporting skills
6. Geography
7. Workplace type
8. Employment type
9. Eligibility

Matching results should be explainable rather than being a single opaque number.

A future matching response can provide information such as:

```text
Role:
Java Backend Engineer

Matched:
Java
Spring Boot
PostgreSQL
Kafka
Kubernetes

Additional:
Docker
GCP

Missing / uncertain:
Terraform

Eligibility:
Region compatible

Workplace:
Remote
```

The objective is to help the user understand *why* an opportunity matched.

---

# Eligibility

Eligibility is modeled separately from matching.

Supported states include:

```text
GLOBAL
REGION_OK
MAYBE
NOT_ELIGIBLE
UNKNOWN
```

Foreign onsite opportunities are not automatically rejected.

Work authorization
