package org.project.nuwabackend.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.type.NotificationType;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "notification_content")
    private String content;

    @Column(name = "notification_url")
    private String url;

    @Column(name = "notification_is_read")
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sender_id")
    private WorkSpaceMember sender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "receiver_id")
    private WorkSpaceMember receiver;

    @Builder
    private Notification(String content, String url, Boolean isRead, NotificationType type, WorkSpaceMember sender, WorkSpaceMember receiver) {
        this.content = content;
        this.url = url;
        this.isRead = isRead;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    // TODO: test code
    public static Notification createDirectNotification(String content, String url, Boolean isRead, WorkSpaceMember sender, WorkSpaceMember receiver) {
        return Notification.builder()
                .content(content)
                .url(url)
                .isRead(isRead)
                .type(NotificationType.DIRECT)
                .sender(sender)
                .receiver(receiver)
                .build();
    }
}
