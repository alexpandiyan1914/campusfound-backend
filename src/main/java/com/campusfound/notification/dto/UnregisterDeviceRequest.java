package com.campusfound.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnregisterDeviceRequest {

    @NotBlank(message = "Push token is required")
    private String pushToken;
}