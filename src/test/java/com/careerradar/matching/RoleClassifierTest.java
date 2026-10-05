package com.careerradar.matching;

import com.careerradar.job.domain.RoleFamily;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleClassifierTest {

    private final RoleClassifier classifier =
            new RoleClassifier();

    @Test
    void shouldClassifyJavaBackendEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior Java Backend Engineer",
                        "Build scalable backend services using Spring Boot and PostgreSQL."
                );

        assertEquals(
                RoleFamily.JAVA_BACKEND,
                result.roleFamily()
        );

        assertTrue(result.confidence() > 0.5);
    }

    @Test
    void shouldNotTreatPythonBackendJobAsJavaBackend() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior Python Backend Engineer",
                        """
                        Build backend services using Python and FastAPI.
                        Experience with Kubernetes, Kafka, GCP and Java is useful.
                        """
                );

        assertEquals(
                RoleFamily.PYTHON_BACKEND,
                result.roleFamily()
        );
    }

    @Test
    void shouldClassifyDataEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior Data Engineer",
                        "Build ETL pipelines and data platforms."
                );

        assertEquals(
                RoleFamily.DATA_ENGINEER,
                result.roleFamily()
        );
    }

    @Test
    void shouldClassifyMachineLearningEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "Machine Learning Engineer",
                        "Develop machine learning models and production ML systems."
                );

        assertEquals(
                RoleFamily.MACHINE_LEARNING,
                result.roleFamily()
        );
    }

    @Test
    void shouldClassifyDevOpsEngineer() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior DevOps Engineer",
                        "Own CI/CD, infrastructure and deployment automation."
                );

        assertEquals(
                RoleFamily.DEVOPS,
                result.roleFamily()
        );
    }

    @Test
    void shouldReturnUnknownWhenNoRoleSignalsExist() {

        RoleClassificationResult result =
                classifier.classify(
                        "Senior Professional",
                        "Work with business stakeholders."
                );

        assertEquals(
                RoleFamily.UNKNOWN,
                result.roleFamily()
        );

        assertEquals(
                0.0,
                result.confidence()
        );
    }
}