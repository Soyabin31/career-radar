package com.careerradar.api;

import com.careerradar.profile.ProfilePreferenceQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile/preferences")
public class ProfilePreferenceController {

    private final ProfilePreferenceQueryService
            preferenceQueryService;

    public ProfilePreferenceController(
            ProfilePreferenceQueryService
                    preferenceQueryService) {

        this.preferenceQueryService =
                preferenceQueryService;
    }

    @GetMapping
    public ProfilePreferenceSummary
    getActiveProfilePreferences() {

        return preferenceQueryService
                .getActiveProfilePreferences();
    }
}