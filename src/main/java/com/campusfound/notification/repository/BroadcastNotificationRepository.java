package com.campusfound.notification.repository;

import com.campusfound.notification.entity.BroadcastNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BroadcastNotificationRepository extends JpaRepository<BroadcastNotification, Long> {
}