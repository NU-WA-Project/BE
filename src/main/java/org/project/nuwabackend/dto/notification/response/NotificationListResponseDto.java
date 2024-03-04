package org.project.nuwabackend.dto.notification.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.project.nuwabackend.type.NotificationType;

import java.time.LocalDateTime;

@Builder
public record NotificationListResponseDto(Long notificationId,
                                          String notificationContent,
                                          String notificationUrl,
                                          Boolean isRead,
                                          NotificationType notificationType,
                                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                                          LocalDateTime createdAt) {
}