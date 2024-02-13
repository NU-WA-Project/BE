package org.project.nuwabackend.domain.channel;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("voice")
public class Voice extends Channel {

    @ManyToOne
    @JoinColumn(name = "create_voice_member_id")
    private WorkSpaceMember createMember;

    @ManyToOne
    @JoinColumn(name = "join_voice_member_id")
    private WorkSpaceMember joinMember;

    @Builder
    public Voice(String name, WorkSpace workSpace, WorkSpaceMember createMember, WorkSpaceMember joinMember) {
        super(name, workSpace);
        this.createMember = createMember;
        this.joinMember = joinMember;
    }

    // TODO: test code
    public static Voice createVoiceChannel(String name, WorkSpace workSpace, WorkSpaceMember createMember, WorkSpaceMember joinMember) {
        return Voice.builder()
                .name(name)
                .workSpace(workSpace)
                .createMember(createMember)
                .joinMember(joinMember)
                .build();
    }
}
