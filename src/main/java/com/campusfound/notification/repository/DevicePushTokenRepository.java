package com.campusfound.notification.repository;

import com.campusfound.notification.entity.DevicePushToken;
import com.campusfound.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DevicePushTokenRepository
        extends JpaRepository<DevicePushToken, Long> {

    Optional<DevicePushToken> findByPushToken(
            String pushToken
    );

    List<DevicePushToken> findByUserAndActiveTrue(
            User user
    );

    Optional<DevicePushToken> findByPushTokenAndUser(
            String pushToken,
            User user
    );
}