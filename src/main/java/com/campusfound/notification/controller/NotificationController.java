package com.campusfound.notification.controller;

import com.campusfound.notification.dto.NotificationResponse;
import com.campusfound.notification.dto.RegisterDeviceRequest;
import com.campusfound.notification.dto.UnregisterDeviceRequest;
import com.campusfound.notification.service.DevicePushTokenService;
import com.campusfound.notification.service.NotificationHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationHistoryService notificationHistoryService;
    private final DevicePushTokenService devicePushTokenService;

    @GetMapping("/my")
    public ResponseEntity<Page<NotificationResponse>>
    getMyNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Page<NotificationResponse> notifications =
                notificationHistoryService
                        .getMyNotifications(
                                authentication.getName(),
                                page,
                                size
                        );

        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/devices")
    public ResponseEntity<Map<String, String>>
    registerDevice(
            Authentication authentication,
            @Valid
            @RequestBody RegisterDeviceRequest request
    ) {

        devicePushTokenService.registerDevice(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Device registered successfully"
                )
        );
    }

    @PostMapping("/devices/unregister")
    public ResponseEntity<Map<String, String>>
    unregisterDevice(
            Authentication authentication,
            @Valid
            @RequestBody UnregisterDeviceRequest request
    ) {

        devicePushTokenService.unregisterDevice(
                authentication.getName(),
                request
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Device unregistered successfully"
                )
        );
    }
}