package com.campusfound.notification.service;

import com.campusfound.notification.dto.NotificationResponse;
import com.campusfound.notification.entity.BroadcastNotification;
import com.campusfound.notification.entity.Notification;
import com.campusfound.notification.repository.BroadcastNotificationRepository;
import com.campusfound.notification.repository.NotificationRepository;
import com.campusfound.user.entity.User;
import com.campusfound.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationHistoryService {

    private final NotificationRepository notificationRepository;
    private final BroadcastNotificationRepository
            broadcastNotificationRepository;
    private final UserRepository userRepository;

    public Page<NotificationResponse> getMyNotifications(
            String email,
            int page,
            int size
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        List<NotificationResponse> combined =
                new ArrayList<>();

        /*
         * Personal notifications
         */
        List<Notification> personalNotifications =
                notificationRepository
                        .findByRecipient(user);

        personalNotifications
                .stream()
                .map(this::toResponse)
                .forEach(combined::add);

        /*
         * Broadcast notifications
         */
        List<BroadcastNotification> broadcastNotifications =
                broadcastNotificationRepository
                        .findAll();

        broadcastNotifications
                .stream()
                .map(this::toResponse)
                .forEach(combined::add);

        /*
         * Newest first
         */
        combined.sort(
                Comparator.comparing(
                        NotificationResponse::getCreatedAt
                ).reversed()
        );

        /*
         * Manual pagination
         */
        int start =
                Math.min(
                        page * size,
                        combined.size()
                );

        int end =
                Math.min(
                        start + size,
                        combined.size()
                );

        List<NotificationResponse> pageContent =
                combined.subList(start, end);

        Pageable pageable =
                PageRequest.of(page, size);

        return new PageImpl<>(
                pageContent,
                pageable,
                combined.size()
        );
    }

    private NotificationResponse toResponse(
            Notification notification
    ) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .claimId(notification.getClaimId())
                .itemId(notification.getItemId())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private NotificationResponse toResponse(
            BroadcastNotification notification
    ) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .claimId(null)
                .itemId(notification.getItemId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}