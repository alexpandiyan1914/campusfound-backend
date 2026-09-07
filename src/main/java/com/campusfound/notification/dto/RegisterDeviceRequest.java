package com.campusfound.notification.dto;

import com.campusfound.notification.enums.DevicePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterDeviceRequest {

    @NotBlank(message = "Push token is required")
    private String pushToken;

    @NotNull(message = "Platform is required")
    private DevicePlatform platform;

    private String deviceName;
}