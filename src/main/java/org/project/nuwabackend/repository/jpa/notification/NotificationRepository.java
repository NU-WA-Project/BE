package org.project.nuwabackend.repository.jpa.notification;

import org.project.nuwabackend.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}