package org.project.nuwabackend.service.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.nuwa.domain.channel.Chat;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.channel.dto.request.ChatChannelJoinMemberRequestDto;
import org.project.nuwabackend.nuwa.channel.dto.request.ChatChannelRequestDto;
import org.project.nuwabackend.nuwa.channel.service.ChatChannelService;
import org.project.nuwabackend.nuwa.channel.repository.jpa.ChatChannelRepository;
import org.project.nuwabackend.nuwa.channel.repository.jpa.ChatJoinMemberRepository;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] Chat Channel Service Test")
@ExtendWith(MockitoExtension.class)
class ChatChannelServiceTest {

    @Mock
    WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    ChatChannelRepository chatChannelRepository;
    @Mock
    ChatJoinMemberRepository chatJoinMemberRepository;

    @InjectMocks
    ChatChannelService chatChannelService;


    Member member;
    WorkSpace workSpace;
    WorkSpaceMember workSpaceMember;
    ChatChannelRequestDto chatChannelRequestDto;
    ChatChannelJoinMemberRequestDto chatChannelJoinMemberRequestDto;
    String email = "abcd@gmail.com";

    @BeforeEach
    void setup() {
        String workSpaceName = "nuwa";
        String workSpaceImage = "N";
        String workSpaceIntroduce = "개발";

        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        String password = "abcd1234";
        String nickname = "nickname";
        String phoneNumber = "01000000000";

        member = Member.createMember(email, password, nickname, phoneNumber);

        String workSpaceMemberName = "abcd";
        String workSpaceMemberJob = "backend";
        String workSpaceMemberImage = "B";

        workSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                workSpaceMemberName,
                workSpaceMemberJob,
                workSpaceMemberImage,
                WorkSpaceMemberType.CREATED,
                member, workSpace);

        ReflectionTestUtils.setField(workSpaceMember, "id", 1L);
        Long workSpaceId = 1L;
        String channelName = "chat";

        chatChannelRequestDto = new ChatChannelRequestDto(workSpaceId, channelName);

        Long chatChannelId = 1L;
        List<Long> joinMemberIdList = new ArrayList<>(List.of(1L));

        chatChannelJoinMemberRequestDto = new ChatChannelJoinMemberRequestDto(chatChannelId, joinMemberIdList);
    }

//    @Test
//    @DisplayName("[Service] Create Chat Channel Test")
//    void createChatChannelTest() {
//        //given
//        String channelName = "chat";
//        Long workSpaceId = 1L;
//
//        Chat chatChannel =
//                Chat.createChatChannel(channelName, workSpace, workSpaceMember);
//
//        given(chatChannelRepository.save(any()))
//                .willReturn(chatChannel);
//        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
//                .willReturn(Optional.of(workSpaceMember));
//
//        //when
//        Long chatChannelId =
//                chatChannelService.createChatChannel(email, chatChannelRequestDto);
//
//        //then
//        assertThat(chatChannelId).isNotNull();
//        verify(chatChannelRepository).save(chatChannel);
//        verify(workSpaceMemberRepository).findByMemberEmailAndWorkSpaceId(email, workSpaceId);
//    }
}