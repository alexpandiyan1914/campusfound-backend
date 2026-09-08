package com.campusfound.notification.dto;

import com.campusfound.notification.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;

    private NotificationType type;

    private String title;

    private String message;

    private Long claimId;

    private Long itemId;

    private LocalDateTime createdAt;
}