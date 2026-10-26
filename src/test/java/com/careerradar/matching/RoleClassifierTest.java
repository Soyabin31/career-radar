package com.careerradar.matching;

import com.careerradar.job.domain.RoleFamily;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleClassifierTest {

    private final RoleClassifier classifier =
            new RoleClassifier();

    @Test
    void shouldClassifySeniorJavaBackendEngineerAsJavaBackend() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior Java Backend Engineer",
                        "Build backend services using Java, Spring Boot, Kafka and PostgreSQL."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.JAVA_BACKEND);

        assertThat(result.confidence())
                .isGreaterThanOrEqualTo(0.45);
    }

    @Test
    void shouldClassifyJavaDeveloperAsJavaBackend() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior Java Developer",
                        "Develop enterprise backend applications."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.JAVA_BACKEND);
    }

    @Test
    void shouldNotClassifyPythonBackendAsJavaBackendWhenJavaIsOnlyMentionedInDescription() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior Python Backend Engineer",
                        """
                        Build APIs using Python and FastAPI.
                        Experience with Java is useful.
                        Kubernetes, Kafka and GCP are also used.
                        """
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.PYTHON_BACKEND);

        assertThat(result.roleFamily())
                .isNotEqualTo(RoleFamily.JAVA_BACKEND);
    }

    @Test
    void shouldClassifyDataEngineerDespiteJavaAndCloudTechnologyMentions() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior Data Engineer",
                        """
                        Build data pipelines using Python, Java, Kafka,
                        Kubernetes and GCP.
                        """
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.DATA_ENGINEER);
    }

    @Test
    void shouldNotClassifyHrOperationsRoleAsJavaBackendBecauseOfJavaMention() {

        RoleClassificationResult result =
                classifier.classify(
                        "HR AI & Automation Operations Lead",
                        """
                        Lead HR automation initiatives.
                        Experience with AI systems is required.
                        Java knowledge is a plus.
                        """
                );

        assertThat(result.roleFamily())
                .isNotEqualTo(RoleFamily.JAVA_BACKEND);
    }

    @Test
    void shouldClassifyDevOpsEngineerAsDevOps() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior DevOps Engineer",
                        "Own CI/CD, Kubernetes and cloud infrastructure."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.DEVOPS);
    }

    @Test
    void shouldClassifySiteReliabilityEngineerAsSre() {

        RoleClassificationResult result =
                classifier.classify(
                        "Site Reliability Engineer",
                        "Improve service reliability and production observability."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.SRE);
    }

    @Test
    void shouldReturnUnknownWhenThereIsNoMeaningfulRoleEvidence() {

        RoleClassificationResult result =
                classifier.classify(
                        "Operations Lead",
                        "Java, Kubernetes, Kafka, GCP."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.UNKNOWN);

        assertThat(result.confidence())
                .isEqualTo(0.0);
    }

    @Test
    void shouldClassifySdeIiAsSoftwareEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "SDE II",
                        "Design and build scalable distributed services."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.SOFTWARE_ENGINEER);

        assertThat(result.confidence())
                .isGreaterThanOrEqualTo(0.45);
    }

    @Test
    void shouldClassifySoftwareDevelopmentEngineerTwoAsSoftwareEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "Software Development Engineer 2",
                        "Build customer-facing high throughput services."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.SOFTWARE_ENGINEER);
    }

    @Test
    void shouldClassifySeniorSdeAsSoftwareEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior SDE",
                        "Architect core distributed systems and mentor junior engineers."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.SOFTWARE_ENGINEER);
    }

    @Test
    void shouldClassifySeIiAsSoftwareEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "SE II",
                        "Deliver clean, well-tested production code."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.SOFTWARE_ENGINEER);
    }

    @Test
    void shouldClassifyBackendSdeIiAsBackendEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "Backend SDE II",
                        "Design resilient APIs and high throughput distributed messaging."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.BACKEND_ENGINEER);
    }

    @Test
    void shouldClassifyJavaSdeIiAsJavaBackend() {

        RoleClassificationResult result =
                classifier.classify(
                        "Java SDE II",
                        "Develop backend services using Java and Spring Boot."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.JAVA_BACKEND);
    }

    @Test
    void shouldClassifyMemberOfTechnicalStaffAsSoftwareEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "Member of Technical Staff (MTS 2)",
                        "Work on database internals and cloud platform infrastructure."
                );

        assertThat(result.roleFamily())
                .isEqualTo(RoleFamily.SOFTWARE_ENGINEER);
    }

    @Test
    void shouldNotFalselyClassifySecurityOrServicesAsSoftwareEngineerDueToShortAcronym() {

        RoleClassificationResult securityResult =
                classifier.classify(
                        "Senior Security Engineer",
                        "Identify vulnerabilities and lead application security."
                );

        assertThat(securityResult.roleFamily())
                .isEqualTo(RoleFamily.SECURITY);

        RoleClassificationResult servicesResult =
                classifier.classify(
                        "Payroll Client Services Specialist",
                        "Coordinate payroll solutions for enterprise clients."
                );

        assertThat(servicesResult.roleFamily())
                .isEqualTo(RoleFamily.UNKNOWN);
    }

    @Test
    void shouldHandleNullAndBlankInput() {

        RoleClassificationResult nullResult =
                classifier.classify(null, null);

        RoleClassificationResult blankResult =
                classifier.classify("   ", "   ");

        assertThat(nullResult.roleFamily())
                .isEqualTo(RoleFamily.UNKNOWN);

        assertThat(blankResult.roleFamily())
                .isEqualTo(RoleFamily.UNKNOWN);
    }
}