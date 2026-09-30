package com.careerradar.source;

import java.time.OffsetDateTime;

public record RawJob(

        String sourceJobId,

        String title,

        String description,

        String companyName,

        String location,

        String country,

        String city,

        String url,

        OffsetDateTime postedAt,

        OffsetDateTime updatedAt

) {
}
