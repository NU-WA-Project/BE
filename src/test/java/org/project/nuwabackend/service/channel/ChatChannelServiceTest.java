package org.project.nuwabackend.service.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.domain.channel.Chat;
import org.project.nuwabackend.domain.channel.ChatJoinMember;
import org.project.nuwabackend.domain.member.Member;
import org.project.nuwabackend.domain.workspace.WorkSpace;
import org.project.nuwabackend.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.dto.channel.request.ChatChannelJoinMemberRequest;
import org.project.nuwabackend.dto.channel.request.ChatChannelRequestDto;
import org.project.nuwabackend.repository.jpa.ChatChannelRepository;
import org.project.nuwabackend.repository.jpa.ChatJoinMemberRepository;
import org.project.nuwabackend.repository.jpa.WorkSpaceMemberRepository;
import org.project.nuwabackend.type.WorkSpaceMemberType;

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
    ChatChannelJoinMemberRequest chatChannelJoinMemberRequest;
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

        Long workSpaceId = 1L;
        String channelName = "chat";

        chatChannelRequestDto = new ChatChannelRequestDto(workSpaceId, channelName);

        Long chatChannelId = 1L;
        List<Long> joinMemberIdList = new ArrayList<>(List.of(1L));

        chatChannelJoinMemberRequest = new ChatChannelJoinMemberRequest(chatChannelId, joinMemberIdList);
    }

    @Test
    @DisplayName("[Service] Create Chat Channel Test")
    void createChatChannelTest() {
        //given
        String channelName = "chat";
        Long workSpaceId = 1L;

        Chat chatChannel =
                Chat.createChatChannel(channelName, workSpace, workSpaceMember);

        given(chatChannelRepository.save(any()))
                .willReturn(chatChannel);
        given(workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(anyString(), any()))
                .willReturn(Optional.of(workSpaceMember));

        //when
        String chatChannelRoomId =
                chatChannelService.createChatChannel(email, chatChannelRequestDto);

        //then
        assertThat(chatChannelRoomId).isNotNull();
        verify(chatChannelRepository).save(chatChannel);
        verify(workSpaceMemberRepository).findByMemberEmailAndWorkSpaceId(email, workSpaceId);
    }

    @Test
    @DisplayName("[Service] Join Chat Channel Test")
    void joinChatChannelTest() {
        //given
        Long chatChannelId = chatChannelJoinMemberRequest.chatChannelId();
        List<Long> joinMemberIdList = chatChannelJoinMemberRequest.joinMemberIdList();
        String channelName = "chat";
        Chat chatChannel =
                Chat.createChatChannel(channelName, workSpace, workSpaceMember);

        given(chatChannelRepository.findById(any()))
                .willReturn(Optional.ofNullable(chatChannel));

        List<ChatJoinMember> chatJoinMemberList = new ArrayList<>();
        for (Long id : joinMemberIdList) {
            given(workSpaceMemberRepository.findById(any()))
                    .willReturn(Optional.of(workSpaceMember));

            ChatJoinMember chatJoinMember = ChatJoinMember.createChatJoinMember(workSpaceMember, chatChannel);
            chatJoinMemberList.add(chatJoinMember);
        }

        given(chatJoinMemberRepository.saveAll(chatJoinMemberList))
                .willReturn(chatJoinMemberList);

        //when
        chatChannelService.joinChatChannel(chatChannelJoinMemberRequest);

        //then
        verify(chatChannelRepository).findById(chatChannelId);
        verify(workSpaceMemberRepository).findById(joinMemberIdList.get(0));
        verify(chatJoinMemberRepository).saveAll(chatJoinMemberList);
    }
}