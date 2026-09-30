package com.careerradar.source;

import java.util.List;

public interface JobSource {

    SourceType type();

    List<RawJob> fetch(JobSourceEntity configuration);
}