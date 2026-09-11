package com.campusfound.app.controller;

import com.campusfound.app.dto.AppVersionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
public class AppVersionController {

    @Value("${campusfound.app.latest-version}")
    private String latestVersion;

    @Value("${campusfound.app.release-url}")
    private String releaseUrl;

    @Value("${campusfound.app.update-message}")
    private String updateMessage;

    @GetMapping("/version")
    public ResponseEntity<AppVersionResponse> getAppVersion() {

        AppVersionResponse response =
                new AppVersionResponse(
                        latestVersion,
                        releaseUrl,
                        updateMessage
                );

        return ResponseEntity.ok(response);
    }
}