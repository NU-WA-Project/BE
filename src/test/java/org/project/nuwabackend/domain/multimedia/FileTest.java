package org.project.nuwabackend.domain.multimedia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.project.nuwabackend.domain.channel.Channel;
import org.project.nuwabackend.domain.channel.Chat;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.type.WorkSpaceMemberType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[Domain] File Domain Test")
class FileTest {

    WorkSpace workSpace;
    String workSpaceName = "workSpaceName";
    String workSpaceImage = "workSpaceImage";
    String workSpaceIntroduce = "workSpaceIntroduce";

    String workSpaceMemberName = "workSpaceMemberName";
    String workSpaceMemberImage = "workSpaceMemberImage";
    String workSpaceMemberJob = "workSpaceJob";

    String memberEmail = "senderEmail";
    String memberPassword = "senderPassword";
    String memberNickname = "senderNickname";
    String memberPhoneNumber = "senderPhoneNumber";

    String channelName = "channelName";
    String fileUrl = "url";

    Member member;
    WorkSpaceMember workSpaceMember;
    Channel channel;

    @BeforeEach
    void setup() {
        member = Member.createMember(memberEmail, memberPassword, memberNickname, memberPhoneNumber);
        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);
        workSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                workSpaceMemberName,
                workSpaceMemberJob,
                workSpaceMemberImage,
                WorkSpaceMemberType.CREATED,
                member, workSpace);
        // 아무 채널이나 생성
        channel = Chat.createChatChannel(channelName, workSpace, workSpaceMember);
    }


    @Test
    @DisplayName("[Domain] Create File Test")
    void createFileTest() {
        //given
        File file = File.createFile(fileUrl, workSpaceMember, workSpace, channel);

        //when
        //then
        assertThat(file.getUrl()).isEqualTo(fileUrl);
        assertThat(file.getChannel()).isEqualTo(channel);
        assertThat(file.getWorkSpace()).isEqualTo(workSpace);
        assertThat(file.getWorkSpaceMember()).isEqualTo(workSpaceMember);
    }
}