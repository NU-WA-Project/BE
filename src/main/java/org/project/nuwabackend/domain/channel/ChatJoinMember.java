package org.project.nuwabackend.domain.channel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatJoinMember {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "chat_join_member_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_join_workspace_id")
    private WorkSpaceMember joinMember;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chat_channel_id")
    private Chat chatChannel;

    @Builder
    private ChatJoinMember(WorkSpaceMember joinMember, Chat chatChannel) {
        this.joinMember = joinMember;
        this.chatChannel = chatChannel;
    }

    // TODO: test code
    // 참여 멤버 생성
    public static ChatJoinMember createChatJoinMember(WorkSpaceMember joinMember, Chat chatChannel) {
        return ChatJoinMember.builder()
                .joinMember(joinMember)
                .chatChannel(chatChannel)
                .build();
    }
}