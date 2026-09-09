package com.campusfound.notification.repository;

import com.campusfound.notification.entity.Notification;
import com.campusfound.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipient(
            User recipient,
            Pageable pageable
    );

    List<Notification> findByRecipient(
            User recipient
    );
}