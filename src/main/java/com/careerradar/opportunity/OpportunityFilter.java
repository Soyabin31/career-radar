package com.careerradar.opportunity;

import com.careerradar.job.domain.EligibilityStatus;
import com.careerradar.job.domain.EmploymentType;
import com.careerradar.job.domain.GeographyType;
import com.careerradar.job.domain.RoleFamily;
import com.careerradar.job.domain.WorkplaceType;

public record OpportunityFilter(
        Double minScore,
        EligibilityStatus eligibilityStatus,
        GeographyType geographyType,
        WorkplaceType workplaceType,
        EmploymentType employmentType,
        RoleFamily roleFamily,
        Integer postedWithinDays
) {

    public static OpportunityFilter empty() {
        return new OpportunityFilter(
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public boolean hasPostedWithinFilter() {
        return postedWithinDays != null && postedWithinDays > 0;
    }
}