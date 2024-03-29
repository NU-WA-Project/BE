package org.project.nuwabackend.nuwa.domain.channel;

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
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VoiceJoinMember {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "voice_join_member_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "voice_join_workspace_member_id")
    private WorkSpaceMember joinMember;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "voice_channel_id")
    private Voice voiceChannel;

    @Builder
    private VoiceJoinMember(WorkSpaceMember joinMember, Voice voiceChannel) {
        this.joinMember = joinMember;
        this.voiceChannel = voiceChannel;
    }

    // 참여 멤버 생성
    public static VoiceJoinMember createVoiceJoinMember(WorkSpaceMember joinMember, Voice voiceChannel) {
        return VoiceJoinMember.builder()
                .joinMember(joinMember)
                .voiceChannel(voiceChannel)
                .build();
    }
}
