package com.campusfound.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ExpoPushService {

    private static final String EXPO_PUSH_URL =
            "https://exp.host/--/api/v2/push/send";

    private static final String ANDROID_CHANNEL_ID =
            "campusfound";

    private final RestClient restClient;

    public ExpoPushService() {
        this.restClient = RestClient.create();
    }

    public void sendPush(
            String pushToken,
            String title,
            String body,
            Map<String, Object> data
    ) {

        try {

            Map<String, Object> payload =
                    new HashMap<>();

            payload.put(
                    "to",
                    pushToken
            );

            payload.put(
                    "title",
                    title
            );

            payload.put(
                    "body",
                    body
            );

            payload.put(
                    "sound",
                    "default"
            );

            /*
             * CampusFound notifications such as
             * claim decisions and new-item alerts
             * should be delivered promptly.
             */
            payload.put(
                    "priority",
                    "high"
            );

            /*
             * Must match the Android notification
             * channel created by the frontend.
             */
            payload.put(
                    "channelId",
                    ANDROID_CHANNEL_ID
            );

            if (
                    data != null &&
                            !data.isEmpty()
            ) {

                payload.put(
                        "data",
                        data
                );
            }

            String response =
                    restClient
                            .post()
                            .uri(
                                    EXPO_PUSH_URL
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .body(
                                    payload
                            )
                            .retrieve()
                            .body(
                                    String.class
                            );

            log.info(
                    "Expo push request completed successfully. Response: {}",
                    response
            );

        } catch (
                Exception exception
        ) {

            log.error(
                    "Failed to send Expo push notification.",
                    exception
            );
        }
    }
}