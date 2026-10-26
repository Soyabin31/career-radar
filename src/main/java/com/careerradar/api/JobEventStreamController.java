package com.careerradar.api;

import com.careerradar.job.event.JobClosedEvent;
import com.careerradar.job.event.JobDiscoveredEvent;
import com.careerradar.job.event.JobUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/jobs")
public class JobEventStreamController {

    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L; // 30 minutes

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        });
        emitter.onError(e -> {
            emitters.remove(emitter);
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        });

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to Career Radar real-time opportunity stream"));
        } catch (Exception e) {
            emitters.remove(emitter);
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        }

        return emitter;
    }

    @EventListener
    public void onJobDiscovered(JobDiscoveredEvent event) {
        broadcast("job-discovered", event);
    }

    @EventListener
    public void onJobUpdated(JobUpdatedEvent event) {
        broadcast("job-updated", event);
    }

    @EventListener
    public void onJobClosed(JobClosedEvent event) {
        broadcast("job-closed", event);
    }

    private void broadcast(String eventName, Object data) {
        java.util.List<SseEmitter> deadEmitters = new java.util.ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (Exception ex) {
                deadEmitters.add(emitter);
                try {
                    emitter.complete();
                } catch (Exception ignored) {
                }
            }
        }
        if (!deadEmitters.isEmpty()) {
            emitters.removeAll(deadEmitters);
        }
    }

    public int getActiveClientCount() {
        return emitters.size();
    }
}
