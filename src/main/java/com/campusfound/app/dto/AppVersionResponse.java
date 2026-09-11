package com.campusfound.app.dto;

public record AppVersionResponse(
        String latestVersion,
        String releaseUrl,
        String message
) {
}