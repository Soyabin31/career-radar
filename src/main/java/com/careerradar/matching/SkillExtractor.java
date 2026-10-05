package com.careerradar.matching;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class SkillExtractor {

    private static final Map<String, List<String>> ALIASES =
            createAliases();

    public List<ExtractedSkill> extract(
            String title,
            String description,
            List<Skill> knownSkills) {

        String normalizedText =
                normalize(
                        safe(title) + " " + safe(description)
                );

        List<ExtractedSkill> extracted =
                new ArrayList<>();

        for (Skill skill : knownSkills) {

            List<String> aliases =
                    ALIASES.getOrDefault(
                            skill.getNormalizedName(),
                            List.of(skill.getName())
                    );

            String matchedAlias = null;

            for (String alias : aliases) {
                if (containsSkill(normalizedText, alias)) {
                    matchedAlias = normalize(alias);
                    break;
                }
            }

            if (matchedAlias == null) {
                continue;
            }

            boolean required =
                    isLikelyRequired(
                            normalizedText,
                            matchedAlias
                    );

            double confidence =
                    required ? 0.95 : 0.70;

            extracted.add(
                    new ExtractedSkill(
                            skill,
                            required,
                            confidence
                    )
            );
        }

        return extracted;
    }

    private boolean containsSkill(
            String text,
            String alias) {

        String normalizedAlias =
                normalize(alias);

        String regex =
                "(^|\\s)"
                        + Pattern.quote(normalizedAlias)
                        + "(?=\\s|$)";

        return Pattern.compile(regex)
                .matcher(text)
                .find();
    }

    private boolean isLikelyRequired(
            String text,
            String skill) {

        String escapedSkill =
                Pattern.quote(skill);

        String[] requiredPatterns = {
                "required\\s+" + escapedSkill,
                escapedSkill + "\\s+required",
                "must have\\s+" + escapedSkill,
                "experience with\\s+" + escapedSkill,
                "experience in\\s+" + escapedSkill,
                "proficient in\\s+" + escapedSkill,
                "strong knowledge of\\s+" + escapedSkill,
                "expertise in\\s+" + escapedSkill
        };

        for (String pattern : requiredPatterns) {

            if (Pattern.compile(
                            pattern,
                            Pattern.CASE_INSENSITIVE
                    )
                    .matcher(text)
                    .find()) {

                return true;
            }
        }

        return false;
    }

    private String normalize(String value) {

        return value
                .toLowerCase(Locale.ROOT)
                .replaceAll(
                        "[^a-z0-9+#/-]+",
                        " "
                )
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private static Map<String, List<String>>
    createAliases() {

        Map<String, List<String>> aliases =
                new HashMap<>();

        aliases.put(
                "java",
                List.of("java")
        );

        aliases.put(
                "spring_boot",
                List.of(
                        "spring boot",
                        "springboot"
                )
        );

        aliases.put(
                "spring_security",
                List.of(
                        "spring security"
                )
        );

        aliases.put(
                "rest",
                List.of(
                        "rest",
                        "rest api",
                        "restful"
                )
        );

        aliases.put(
                "microservices",
                List.of(
                        "microservices",
                        "microservice"
                )
        );

        aliases.put(
                "distributed_systems",
                List.of(
                        "distributed systems"
                )
        );

        aliases.put(
                "postgresql",
                List.of(
                        "postgresql",
                        "postgres"
                )
        );

        aliases.put(
                "mysql",
                List.of("mysql")
        );

        aliases.put(
                "kafka",
                List.of("kafka")
        );

        aliases.put(
                "google_pubsub",
                List.of(
                        "google pub sub",
                        "google pub/sub",
                        "pub/sub",
                        "pubsub"
                )
        );

        aliases.put(
                "kubernetes",
                List.of(
                        "kubernetes",
                        "k8s"
                )
        );

        aliases.put(
                "docker",
                List.of("docker")
        );

        aliases.put(
                "aws",
                List.of(
                        "aws",
                        "amazon web services"
                )
        );

        aliases.put(
                "gcp",
                List.of(
                        "gcp",
                        "google cloud",
                        "google cloud platform"
                )
        );

        aliases.put(
                "terraform",
                List.of("terraform")
        );

        aliases.put(
                "oauth2",
                List.of(
                        "oauth2",
                        "oauth 2",
                        "oauth"
                )
        );

        aliases.put(
                "jwt",
                List.of(
                        "jwt",
                        "json web token",
                        "json web tokens"
                )
        );

        aliases.put(
                "python",
                List.of("python")
        );

        aliases.put(
                "pandas",
                List.of("pandas")
        );

        aliases.put(
                "fastapi",
                List.of("fastapi")
        );

        return aliases;
    }

    public record ExtractedSkill(
            Skill skill,
            boolean required,
            double confidence) {
    }
}