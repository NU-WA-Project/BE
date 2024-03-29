package org.project.nuwabackend.nuwa.domain.channel;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("voice")
public class Voice extends Channel {

    @Builder
    private Voice(String name, WorkSpace workSpace, WorkSpaceMember createMember, Boolean isCreateMemberDelete) {
        super(name, workSpace, createMember, isCreateMemberDelete);
    }

    public static Voice createVoiceChannel(String name, WorkSpace workSpace, WorkSpaceMember createMember) {
        return Voice.builder()
                .name(name)
                .workSpace(workSpace)
                .createMember(createMember)
                .build();
    }
}
