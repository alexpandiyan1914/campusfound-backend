package com.campusfound.notification.service;

import com.campusfound.notification.entity.DevicePushToken;
import com.campusfound.notification.entity.Notification;
import com.campusfound.notification.enums.NotificationType;
import com.campusfound.notification.repository.DevicePushTokenRepository;
import com.campusfound.notification.repository.NotificationRepository;
import com.campusfound.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DevicePushTokenRepository devicePushTokenRepository;
    private final ExpoPushService expoPushService;

    public void notifyClaimApproved(
            User recipient,
            Long claimId,
            Long itemId,
            String itemTitle
    ) {

        String title = "Claim Approved";

        String message =
                "Your claim for \"" +
                        itemTitle +
                        "\" has been approved. " +
                        "Visit the official Lost & Found office " +
                        "with your college ID for physical verification.";

        createNotification(
                recipient,
                NotificationType.CLAIM_APPROVED,
                title,
                message,
                claimId,
                itemId
        );
    }

    public void notifyClaimRejected(
            User recipient,
            Long claimId,
            Long itemId,
            String itemTitle
    ) {

        String title = "Claim Rejected";

        String message =
                "Your claim for \"" +
                        itemTitle +
                        "\" could not be verified. " +
                        "Open CampusFound to view your claim details.";

        createNotification(
                recipient,
                NotificationType.CLAIM_REJECTED,
                title,
                message,
                claimId,
                itemId
        );
    }

    public void notifyNewItem(
            User recipient,
            Long itemId,
            String itemTitle
    ) {

        String title = "New Item Posted";

        String message =
                "A new \"" +
                        itemTitle +
                        "\" has been added to CampusFound.";

        createNotification(
                recipient,
                NotificationType.NEW_ITEM,
                title,
                message,
                null,
                itemId
        );
    }

    private void createNotification(
            User recipient,
            NotificationType type,
            String title,
            String message,
            Long claimId,
            Long itemId
    ) {

        Notification notification =
                Notification.builder()
                        .recipient(recipient)
                        .type(type)
                        .title(title)
                        .message(message)
                        .claimId(claimId)
                        .itemId(itemId)
                        .build();

        notificationRepository.save(notification);

        executePushAfterCommit(
                recipient,
                type,
                title,
                message,
                claimId,
                itemId
        );
    }

    private void executePushAfterCommit(
            User recipient,
            NotificationType type,
            String title,
            String message,
            Long claimId,
            Long itemId
    ) {

        if (TransactionSynchronizationManager
                .isActualTransactionActive()) {

            TransactionSynchronizationManager
                    .registerSynchronization(
                            new TransactionSynchronization() {

                                @Override
                                public void afterCommit() {
                                    sendPushToActiveDevices(
                                            recipient,
                                            type,
                                            title,
                                            message,
                                            claimId,
                                            itemId
                                    );
                                }
                            }
                    );

        } else {

            sendPushToActiveDevices(
                    recipient,
                    type,
                    title,
                    message,
                    claimId,
                    itemId
            );
        }
    }

    private void sendPushToActiveDevices(
            User recipient,
            NotificationType type,
            String title,
            String message,
            Long claimId,
            Long itemId
    ) {

        try {

            List<DevicePushToken> activeTokens =
                    devicePushTokenRepository
                            .findByUserAndActiveTrue(recipient);

            if (activeTokens.isEmpty()) {

                log.info(
                        "No active push tokens found for user: {}",
                        recipient.getEmail()
                );

                return;
            }

            Map<String, Object> data =
                    new HashMap<>();

            data.put("type", type.name());

            if (claimId != null) {
                data.put("claimId", claimId);
            }

            if (itemId != null) {
                data.put("itemId", itemId);
            }

            for (DevicePushToken device : activeTokens) {

                expoPushService.sendPush(
                        device.getPushToken(),
                        title,
                        message,
                        data
                );
            }

        } catch (Exception exception) {

            log.error(
                    "Push delivery failed for user: {}",
                    recipient.getEmail(),
                    exception
            );
        }
    }
}