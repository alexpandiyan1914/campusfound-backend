package com.campusfound.notification.service;

import com.campusfound.notification.dto.RegisterDeviceRequest;
import com.campusfound.notification.dto.UnregisterDeviceRequest;
import com.campusfound.notification.entity.DevicePushToken;
import com.campusfound.notification.repository.DevicePushTokenRepository;
import com.campusfound.user.entity.User;
import com.campusfound.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DevicePushTokenService {

    private final DevicePushTokenRepository devicePushTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerDevice(
            String email,
            RegisterDeviceRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        DevicePushToken devicePushToken =
                devicePushTokenRepository
                        .findByPushToken(request.getPushToken())
                        .orElse(null);

        if (devicePushToken == null) {

            devicePushToken = DevicePushToken.builder()
                    .user(user)
                    .pushToken(request.getPushToken())
                    .platform(request.getPlatform())
                    .deviceName(request.getDeviceName())
                    .active(true)
                    .build();

        } else {

            devicePushToken.setUser(user);
            devicePushToken.setPlatform(
                    request.getPlatform()
            );
            devicePushToken.setDeviceName(
                    request.getDeviceName()
            );
            devicePushToken.setActive(true);
        }

        devicePushTokenRepository.save(devicePushToken);
    }

    @Transactional
    public void unregisterDevice(
            String email,
            UnregisterDeviceRequest request
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        DevicePushToken devicePushToken =
                devicePushTokenRepository
                        .findByPushTokenAndUser(
                                request.getPushToken(),
                                user
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Device token not found"
                                )
                        );

        devicePushToken.setActive(false);

        devicePushTokenRepository.save(devicePushToken);
    }
}