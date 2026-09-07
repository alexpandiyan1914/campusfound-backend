package com.campusfound.notification.service;

import com.campusfound.notification.dto.NotificationResponse;
import com.campusfound.notification.entity.Notification;
import com.campusfound.notification.repository.NotificationRepository;
import com.campusfound.user.entity.User;
import com.campusfound.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationHistoryService {

    private final NotificationRepository notificationRepository;
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

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );

        return notificationRepository
                .findByRecipient(user, pageable)
                .map(this::toResponse);
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
}