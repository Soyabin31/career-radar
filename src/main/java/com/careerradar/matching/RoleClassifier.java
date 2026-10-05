package com.careerradar.matching;

import com.careerradar.job.domain.RoleFamily;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class RoleClassifier {

    private static final Map<RoleFamily, List<String>> ROLE_KEYWORDS =
            createRoleKeywords();

    public RoleClassificationResult classify(
            String title,
            String description) {

        String normalizedTitle = normalize(title);
        String normalizedDescription = normalize(description);

        Map<RoleFamily, Double> scores =
                new EnumMap<>(RoleFamily.class);

        for (RoleFamily roleFamily : ROLE_KEYWORDS.keySet()) {

            double score =
                    calculateScore(
                            roleFamily,
                            normalizedTitle,
                            normalizedDescription
                    );

            if (score > 0) {
                scores.put(roleFamily, score);
            }
        }

        if (scores.isEmpty()) {
            return new RoleClassificationResult(
                    RoleFamily.UNKNOWN,
                    0.0
            );
        }

        Map.Entry<RoleFamily, Double> best =
                scores.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElseThrow();

        double confidence =
                calculateConfidence(
                        best.getValue(),
                        scores.values()
                );

        return new RoleClassificationResult(
                best.getKey(),
                confidence
        );
    }

    private double calculateScore(
            RoleFamily roleFamily,
            String title,
            String description) {

        double score = 0.0;

        for (String keyword : ROLE_KEYWORDS.get(roleFamily)) {

            if (containsPhrase(title, keyword)) {
                score += 5.0;
            }

            if (containsPhrase(description, keyword)) {
                score += 1.0;
            }
        }

        /*
         * Specific technology + role combinations should
         * outrank generic backend classifications.
         */
        if (roleFamily == RoleFamily.PYTHON_BACKEND &&
                containsPhrase(title, "python") &&
                containsPhrase(title, "backend")) {

            score += 8.0;
        }

        if (roleFamily == RoleFamily.JAVA_BACKEND &&
                containsPhrase(title, "java") &&
                containsPhrase(title, "backend")) {

            score += 8.0;
        }

        if (roleFamily == RoleFamily.BACKEND_ENGINEER &&
                containsPhrase(title, "backend engineer") &&
                !containsPhrase(title, "java") &&
                !containsPhrase(title, "python")) {

            score += 3.0;
        }

        return score;
    }

    private double calculateConfidence(
            double winningScore,
            Collection<Double> scores) {

        double totalScore =
                scores.stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();

        if (totalScore <= 0) {
            return 0.0;
        }

        return Math.min(
                1.0,
                winningScore / totalScore
        );
    }

    private boolean containsPhrase(
            String text,
            String phrase) {

        return text.contains(phrase);
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        return value
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9+#./-]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static Map<RoleFamily, List<String>> createRoleKeywords() {

        Map<RoleFamily, List<String>> keywords =
                new EnumMap<>(RoleFamily.class);

        keywords.put(
                RoleFamily.JAVA_BACKEND,
                List.of(
                        "java",
                        "spring boot",
                        "spring framework",
                        "java backend",
                        "java developer",
                        "java engineer",
                        "backend java"
                )
        );

        keywords.put(
                RoleFamily.JAVA_PLATFORM,
                List.of(
                        "java platform",
                        "platform engineer",
                        "platform development",
                        "backend platform",
                        "distributed systems",
                        "platform services"
                )
        );

        keywords.put(
                RoleFamily.BACKEND_ENGINEER,
                List.of(
                        "backend engineer",
                        "backend developer",
                        "backend services",
                        "backend systems",
                        "server side",
                        "server-side"
                )
        );

        keywords.put(
                RoleFamily.SOFTWARE_ENGINEER,
                List.of(
                        "software engineer",
                        "software developer",
                        "software development",
                        "application engineer"
                )
        );

        keywords.put(
                RoleFamily.PYTHON_BACKEND,
                List.of(
                        "python backend",
                        "python developer",
                        "python engineer",
                        "django",
                        "flask",
                        "fastapi"
                )
        );

        keywords.put(
                RoleFamily.PYTHON_DATA,
                List.of(
                        "python data",
                        "pandas",
                        "numpy",
                        "data analysis",
                        "data analyst"
                )
        );

        keywords.put(
                RoleFamily.DATA_ENGINEER,
                List.of(
                        "data engineer",
                        "data engineering",
                        "data pipelines",
                        "etl",
                        "elt",
                        "data platform"
                )
        );

        keywords.put(
                RoleFamily.DATA_SCIENCE,
                List.of(
                        "data scientist",
                        "data science",
                        "statistical modeling",
                        "statistics"
                )
        );

        keywords.put(
                RoleFamily.MACHINE_LEARNING,
                List.of(
                        "machine learning",
                        "machine learning engineer",
                        "ml engineer",
                        "deep learning",
                        "model training",
                        "mlops"
                )
        );

        keywords.put(
                RoleFamily.DEVOPS,
                List.of(
                        "devops",
                        "devops engineer",
                        "ci cd",
                        "ci/cd",
                        "continuous integration",
                        "continuous delivery"
                )
        );

        keywords.put(
                RoleFamily.SRE,
                List.of(
                        "site reliability",
                        "site reliability engineer",
                        "sre",
                        "reliability engineering"
                )
        );

        keywords.put(
                RoleFamily.CLOUD_ENGINEER,
                List.of(
                        "cloud engineer",
                        "cloud infrastructure",
                        "cloud architect",
                        "cloud platform"
                )
        );

        keywords.put(
                RoleFamily.FRONTEND,
                List.of(
                        "frontend",
                        "front-end",
                        "frontend engineer",
                        "frontend developer",
                        "react developer",
                        "angular developer"
                )
        );

        keywords.put(
                RoleFamily.MOBILE,
                List.of(
                        "android developer",
                        "android engineer",
                        "ios developer",
                        "ios engineer",
                        "mobile developer",
                        "mobile engineer"
                )
        );

        keywords.put(
                RoleFamily.QA,
                List.of(
                        "qa engineer",
                        "quality assurance",
                        "test automation",
                        "automation tester",
                        "software tester"
                )
        );

        keywords.put(
                RoleFamily.SECURITY,
                List.of(
                        "security engineer",
                        "application security",
                        "cyber security",
                        "cybersecurity",
                        "security analyst"
                )
        );

        return keywords;
    }
}