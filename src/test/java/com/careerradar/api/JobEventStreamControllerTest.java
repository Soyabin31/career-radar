package com.careerradar.api;

import com.careerradar.job.event.JobClosedEvent;
import com.careerradar.job.event.JobDiscoveredEvent;
import com.careerradar.job.event.JobUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JobEventStreamControllerTest {

    private JobEventStreamController controller;

    @BeforeEach
    void setUp() {
        controller = new JobEventStreamController();
    }

    @Test
    void shouldRegisterEmitterOnStream() {
        SseEmitter emitter = controller.streamEvents();
        assertNotNull(emitter);
        assertEquals(1, controller.getActiveClientCount());
    }

    @Test
    void shouldBroadcastEventsWithoutErrors() {
        controller.streamEvents();

        JobDiscoveredEvent discoveredEvent = new JobDiscoveredEvent(
                1L,
                10L,
                "job-100",
                "Principal Architect",
                "TechCorp",
                "Remote",
                "https://techcorp.com/jobs/100",
                OffsetDateTime.now()
        );

        JobUpdatedEvent updatedEvent = new JobUpdatedEvent(
                1L,
                10L,
                "job-100",
                "Principal Architect",
                "TechCorp"
        );

        JobClosedEvent closedEvent = new JobClosedEvent(
                1L,
                10L,
                "job-100",
                "Principal Architect"
        );

        controller.onJobDiscovered(discoveredEvent);
        controller.onJobUpdated(updatedEvent);
        controller.onJobClosed(closedEvent);

        assertEquals(1, controller.getActiveClientCount());
    }
}
