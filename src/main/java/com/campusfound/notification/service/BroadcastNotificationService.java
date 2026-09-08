package com.campusfound.notification.service;

import com.campusfound.notification.entity.BroadcastNotification;
import com.campusfound.notification.entity.DevicePushToken;
import com.campusfound.notification.enums.NotificationType;
import com.campusfound.notification.repository.BroadcastNotificationRepository;
import com.campusfound.notification.repository.DevicePushTokenRepository;
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
public class BroadcastNotificationService {

    private final BroadcastNotificationRepository
            broadcastNotificationRepository;

    private final DevicePushTokenRepository
            devicePushTokenRepository;

    private final ExpoPushService expoPushService;

    public void notifyNewItem(
            Long itemId,
            String itemTitle
    ) {

        String title =
                "New Item Posted";

        String message =
                "A new \"" +
                        itemTitle +
                        "\" has been added to CampusFound.";

        BroadcastNotification notification =
                BroadcastNotification.builder()
                        .type(NotificationType.NEW_ITEM)
                        .title(title)
                        .message(message)
                        .itemId(itemId)
                        .build();

        broadcastNotificationRepository.save(notification);

        executePushAfterCommit(
                title,
                message,
                itemId
        );
    }

    private void executePushAfterCommit(
            String title,
            String message,
            Long itemId
    ) {

        if (TransactionSynchronizationManager
                .isActualTransactionActive()) {

            TransactionSynchronizationManager
                    .registerSynchronization(
                            new TransactionSynchronization() {

                                @Override
                                public void afterCommit() {

                                    sendPushToStudents(
                                            title,
                                            message,
                                            itemId
                                    );
                                }
                            }
                    );

        } else {

            sendPushToStudents(
                    title,
                    message,
                    itemId
            );
        }
    }

    private void sendPushToStudents(
            String title,
            String message,
            Long itemId
    ) {

        try {

            List<DevicePushToken> tokens =
                    devicePushTokenRepository
                            .findAll()
                            .stream()
                            .filter(DevicePushToken::isActive)
                            .filter(token ->
                                    token.getUser() != null
                                            && token.getUser()
                                            .getRole() != null
                                            && token.getUser()
                                            .getRole()
                                            .name()
                                            .equals("STUDENT")
                            )
                            .toList();

            if (tokens.isEmpty()) {

                log.info(
                        "No active student push tokens found"
                );

                return;
            }

            Map<String, Object> data =
                    new HashMap<>();

            data.put(
                    "type",
                    NotificationType.NEW_ITEM.name()
            );

            data.put(
                    "itemId",
                    itemId
            );

            for (DevicePushToken token : tokens) {

                expoPushService.sendPush(
                        token.getPushToken(),
                        title,
                        message,
                        data
                );
            }

        } catch (Exception exception) {

            log.error(
                    "Failed to send NEW_ITEM broadcast push",
                    exception
            );
        }
    }
}