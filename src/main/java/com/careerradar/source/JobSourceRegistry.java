package com.careerradar.source;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class JobSourceRegistry {

    private final Map<SourceType, JobSource> sources;

    public JobSourceRegistry(List<JobSource> jobSources) {

        this.sources = new EnumMap<>(SourceType.class);

        for (JobSource source : jobSources) {
            JobSource previous = sources.put(source.type(), source);

            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate JobSource implementation for type: "
                                + source.type()
                );
            }
        }
    }

    public JobSource get(SourceType type) {

        JobSource source = sources.get(type);

        if (source == null) {
            throw new IllegalArgumentException(
                    "No JobSource registered for type: " + type
            );
        }

        return source;
    }
}