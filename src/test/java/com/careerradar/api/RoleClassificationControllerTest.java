package com.careerradar.api;

import com.careerradar.job.domain.RoleFamily;
import com.careerradar.matching.RoleClassificationResponse;
import com.careerradar.matching.RoleClassificationResult;
import com.careerradar.matching.RoleClassificationService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoleClassificationControllerTest {

    @Test
    void shouldClassifySingleJob() {

        RoleClassificationService service =
                mock(RoleClassificationService.class);

        when(service.classifyJob(1L))
                .thenReturn(
                        new RoleClassificationResult(
                                RoleFamily.JAVA_BACKEND,
                                0.95
                        )
                );

        RoleClassificationController controller =
                new RoleClassificationController(service);

        RoleClassificationResponse response =
                controller.classifyJob(1L);

        assertEquals(
                1L,
                response.jobId()
        );

        assertEquals(
                "JAVA_BACKEND",
                response.roleFamily()
        );

        assertEquals(
                BigDecimal.valueOf(0.95),
                response.confidence()
        );

        assertEquals(
                "RULE_BASED",
                response.source()
        );

        verify(service)
                .classifyJob(1L);
    }

    @Test
    void shouldReturnClassificationsForJob() {

        RoleClassificationService service =
                mock(RoleClassificationService.class);

        var classification =
                mock(com.careerradar.matching.JobRoleClassification.class);

        when(classification.getJob())
                .thenReturn(mock(com.careerradar.job.Job.class));

        when(classification.getRoleFamily())
                .thenReturn(RoleFamily.JAVA_BACKEND);

        when(classification.getConfidence())
                .thenReturn(BigDecimal.valueOf(0.95));

        when(classification.getSource())
                .thenReturn("RULE_BASED");

        when(service.getClassifications(1L))
                .thenReturn(List.of(classification));

        RoleClassificationController controller =
                new RoleClassificationController(service);

        List<RoleClassificationResponse> response =
                controller.getClassifications(1L);

        assertEquals(
                1,
                response.size()
        );

        assertEquals(
                "JAVA_BACKEND",
                response.get(0).roleFamily()
        );

        assertEquals(
                BigDecimal.valueOf(0.95),
                response.get(0).confidence()
        );

        verify(service)
                .getClassifications(1L);
    }
}