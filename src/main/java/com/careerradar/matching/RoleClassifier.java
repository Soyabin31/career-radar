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

    private static final double TITLE_KEYWORD_WEIGHT = 10.0;
    private static final double DESCRIPTION_ROLE_WEIGHT = 2.0;

    private static final double MIN_CLASSIFICATION_SCORE = 8.0;
    private static final double MIN_CONFIDENCE = 0.45;

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
            return unknown();
        }

        Map.Entry<RoleFamily, Double> best =
                scores.entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElseThrow();

        /*
         * A role classification must have meaningful role evidence.
         * Technology-only mentions in a description are deliberately
         * insufficient to classify a job as Java/Python/etc.
         */
        if (best.getValue() < MIN_CLASSIFICATION_SCORE) {
            return unknown();
        }

        double confidence =
                calculateConfidence(
                        best.getValue(),
                        scores.values()
                );

        /*
         * When multiple role families are competing closely,
         * do not pretend that the classifier is certain.
         */
        if (confidence < MIN_CONFIDENCE) {
            return unknown();
        }

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
                score += TITLE_KEYWORD_WEIGHT;
            }

            if (containsPhrase(description, keyword)) {
                score += DESCRIPTION_ROLE_WEIGHT;
            }
        }

        /*
         * Explicit title combinations are much stronger evidence
         * than individual technology mentions.
         */
        score += titleSpecificBonus(
                roleFamily,
                title
        );

        return score;
    }

    private double titleSpecificBonus(
            RoleFamily roleFamily,
            String title) {

        if (roleFamily == RoleFamily.JAVA_BACKEND &&
                containsAny(
                        title,
                        "java backend",
                        "java developer",
                        "java engineer",
                        "backend java",
                        "java sde",
                        "java software development engineer",
                        "java se"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.PYTHON_BACKEND &&
                containsAny(
                        title,
                        "python backend",
                        "python developer",
                        "python engineer"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.BACKEND_ENGINEER &&
                containsAny(
                        title,
                        "backend engineer",
                        "backend developer",
                        "backend sde",
                        "backend software development engineer",
                        "backend se"
                ) &&
                !containsAny(
                        title,
                        "java",
                        "python"
                )) {

            return 12.0;
        }

        if (roleFamily == RoleFamily.DATA_ENGINEER &&
                containsAny(
                        title,
                        "data engineer",
                        "data engineering"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.DATA_SCIENCE &&
                containsAny(
                        title,
                        "data scientist",
                        "data science"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.MACHINE_LEARNING &&
                containsAny(
                        title,
                        "machine learning",
                        "ml engineer"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.DEVOPS &&
                containsAny(
                        title,
                        "devops",
                        "devops engineer"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.SRE &&
                containsAny(
                        title,
                        "site reliability",
                        "site reliability engineer",
                        "sre"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.CLOUD_ENGINEER &&
                containsAny(
                        title,
                        "cloud engineer",
                        "cloud architect"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.FRONTEND &&
                containsAny(
                        title,
                        "frontend",
                        "front-end"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.MOBILE &&
                containsAny(
                        title,
                        "android",
                        "ios",
                        "mobile"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.SECURITY &&
                containsAny(
                        title,
                        "security engineer",
                        "security analyst",
                        "application security",
                        "cybersecurity"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.QA &&
                containsAny(
                        title,
                        "qa engineer",
                        "quality assurance",
                        "test automation"
                )) {

            return 15.0;
        }

        /*
         * Generic platform engineering is kept separate from Java
         * backend. The presence of "platform engineer" alone must not
         * manufacture a Java classification.
         */
        if (roleFamily == RoleFamily.JAVA_PLATFORM &&
                containsAny(
                        title,
                        "java platform",
                        "backend platform"
                )) {

            return 15.0;
        }

        if (roleFamily == RoleFamily.SOFTWARE_ENGINEER &&
                containsAny(
                        title,
                        "software engineer",
                        "software developer",
                        "software development engineer",
                        "sde",
                        "sde ii",
                        "sde 2",
                        "sde iii",
                        "sde 3",
                        "se",
                        "se ii",
                        "se 2",
                        "se iii",
                        "se 3",
                        "senior sde",
                        "senior se",
                        "mts",
                        "member of technical staff"
                ) &&
                !containsAny(
                        title,
                        "backend",
                        "java",
                        "python",
                        "frontend",
                        "front-end",
                        "qa",
                        "data",
                        "devops",
                        "sre",
                        "cloud",
                        "security"
                )) {

            return 10.0;
        }

        return 0.0;
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

    private boolean containsAny(
            String text,
            String... phrases) {

        for (String phrase : phrases) {

            if (containsPhrase(text, phrase)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsPhrase(
            String text,
            String phrase) {

        if (text == null || text.isBlank() || phrase == null || phrase.isBlank()) {
            return false;
        }

        String paddedText = " " + text + " ";
        String paddedPhrase = " " + phrase.trim().toLowerCase(Locale.ROOT) + " ";

        return paddedText.contains(paddedPhrase);
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

    private RoleClassificationResult unknown() {

        return new RoleClassificationResult(
                RoleFamily.UNKNOWN,
                0.0
        );
    }

    private static Map<RoleFamily, List<String>> createRoleKeywords() {

        Map<RoleFamily, List<String>> keywords =
                new EnumMap<>(RoleFamily.class);

        keywords.put(
                RoleFamily.JAVA_BACKEND,
                List.of(
                        "java backend",
                        "java developer",
                        "java engineer",
                        "backend java",
                        "java sde",
                        "java sde 1",
                        "java sde 2",
                        "java sde ii",
                        "java sde 3",
                        "java sde iii",
                        "java sde-1",
                        "java sde-2",
                        "java sde-ii",
                        "java sde-3",
                        "java sde-iii",
                        "java software development engineer",
                        "java se",
                        "java se 1",
                        "java se 2",
                        "java se ii",
                        "java se 3",
                        "java se iii"
                )
        );

        keywords.put(
                RoleFamily.JAVA_PLATFORM,
                List.of(
                        "java platform",
                        "backend platform",
                        "platform development",
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
                        "server-side",
                        "backend sde",
                        "backend sde 1",
                        "backend sde 2",
                        "backend sde ii",
                        "backend sde 3",
                        "backend sde iii",
                        "backend sde-1",
                        "backend sde-2",
                        "backend sde-ii",
                        "backend sde-3",
                        "backend sde-iii",
                        "backend software development engineer",
                        "backend se",
                        "backend se 1",
                        "backend se 2",
                        "backend se ii",
                        "backend se 3",
                        "backend se iii"
                )
        );

        keywords.put(
                RoleFamily.SOFTWARE_ENGINEER,
                List.of(
                        "software engineer",
                        "software developer",
                        "software development",
                        "application engineer",
                        "software development engineer",
                        "software development engineer 1",
                        "software development engineer 2",
                        "software development engineer ii",
                        "software development engineer 3",
                        "software development engineer iii",
                        "sde",
                        "sde 1",
                        "sde 2",
                        "sde 3",
                        "sde i",
                        "sde ii",
                        "sde iii",
                        "sde-1",
                        "sde-2",
                        "sde-3",
                        "sde-i",
                        "sde-ii",
                        "sde-iii",
                        "sde1",
                        "sde2",
                        "sde3",
                        "senior sde",
                        "staff sde",
                        "lead sde",
                        "principal sde",
                        "se",
                        "se 1",
                        "se 2",
                        "se 3",
                        "se i",
                        "se ii",
                        "se iii",
                        "se-1",
                        "se-2",
                        "se-3",
                        "se-i",
                        "se-ii",
                        "se-iii",
                        "se1",
                        "se2",
                        "se3",
                        "senior se",
                        "staff se",
                        "lead se",
                        "principal se",
                        "mts",
                        "mts 1",
                        "mts 2",
                        "mts 3",
                        "mts i",
                        "mts ii",
                        "mts iii",
                        "senior mts",
                        "staff mts",
                        "member of technical staff"
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