package org.project.nuwabackend.service.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.nuwabackend.nuwa.domain.member.Member;
import org.project.nuwabackend.nuwa.domain.mongo.DirectMessage;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpace;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.websocket.dto.request.DirectMessageRequestDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.DirectMessageResponseDto;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.websocket.repository.DirectMessageRepository;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.channel.service.DirectChannelRedisService;
import org.project.nuwabackend.nuwa.notification.service.NotificationService;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;
import org.project.nuwabackend.nuwa.workspacemember.type.WorkSpaceMemberType;
import org.project.nuwabackend.nuwa.websocket.service.DirectMessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("[Service] Direct Message Service Test")
@ExtendWith(MockitoExtension.class)
class DirectMessageServiceTest {

    @Mock
    DirectMessageRepository directMessageRepository;
    @Mock
    DirectChannelRedisService directChannelRedisService;
    @Mock
    WorkSpaceMemberRepository workSpaceMemberRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    DirectMessageService directMessageService;

    DirectMessageRequestDto directMessageRequestDto;
    DirectMessageResponseDto directMessageResponseDto;
    Member member;
    WorkSpace workSpace;
    WorkSpaceMember workSpaceMember;

    @BeforeEach
    void setup() {

        String nickname = "nickname";
        String email = "email";
        String password = "password";
        String phoneNumber = "01000000000";


        Long workSpaceId = 1L;
        String directChannelRoomId = "directChannelRoomId";
        Long senderId = 1L;
        Long receiverId = 2L;
        String directChannelContent = "directChannelContent";
        Long readCount = 1L;
        String senderName = "senderName";
        List<String> rawString = new ArrayList<>(List.of("rawString"));


        directMessageRequestDto = DirectMessageRequestDto.builder()
                .roomId(directChannelRoomId)
                .receiverId(receiverId)
                .content(directChannelContent)
                .build();

        directMessageResponseDto = DirectMessageResponseDto.builder()
                .workSpaceId(workSpaceId)
                .roomId(directChannelRoomId)
                .senderId(senderId)
                .senderName(senderName)
                .content(directChannelContent)
                .rawString(rawString)
                .readCount(readCount)
                .createdAt(LocalDateTime.now())
                .build();

        member = Member.createMember(
                email,
                password,
                nickname,
                phoneNumber);

        String workSpaceName = "nuwa";
        String workSpaceImage = "N";
        String workSpaceIntroduce = "개발";
        workSpace = WorkSpace.createWorkSpace(workSpaceName, workSpaceImage, workSpaceIntroduce);

        String workSpaceMemberName = "abcd";
        String workSpaceMemberJob = "backend";
        String workSpaceMemberImage = "B";

        workSpaceMember = WorkSpaceMember.createWorkSpaceMember(
                workSpaceMemberName,
                workSpaceMemberJob,
                workSpaceMemberImage,
                WorkSpaceMemberType.CREATED,
                member, workSpace);
    }


//    @Test
//    @DisplayName("[Service] Save Direct Message Test")
//    void saveDirectMessageTest() {
//        //given
//        DirectMessage directMessage = DirectMessage.createDirectMessage(
//                directMessageResponseDto.getWorkSpaceId(),
//                directMessageResponseDto.getRoomId(),
//                directMessageResponseDto.getSenderId(),
//                directMessageResponseDto.getSenderName(),
//                directMessageResponseDto.getContent(),
//                directMessageResponseDto.getRawString(),
//                directMessageResponseDto.getReadCount(),
//                MessageType.TEXT,
//                LocalDateTime.now());
//
//        given(directMessageRepository.save(any()))
//                .willReturn(directMessage);
//
//        //when
//        directMessageService.saveDirectMessage(directMessageResponseDto);
//
//        //then
//        verify(directMessageRepository).save(directMessage);
//    }

    @Test
    @DisplayName("[Service] Direct Message Slice Sort By Date")
    void directMessageSlice() {
        //given
        String directChannelRoomId = "directChannelRoomId";

        DirectMessage directMessage = DirectMessage.createDirectMessage(
                directMessageResponseDto.getWorkSpaceId(),
                directMessageResponseDto.getRoomId(),
                directMessageResponseDto.getSenderId(),
                directMessageResponseDto.getSenderName(),
                directMessageResponseDto.getContent(),
                directMessageResponseDto.getRawString(),
                directMessageResponseDto.getReadCount(),
                MessageType.TEXT,
                LocalDateTime.now());

        List<DirectMessage> directMessageList =
                new ArrayList<>(List.of(directMessage));

        PageRequest pageRequest =
                PageRequest.of(0, 10, Sort.by("createdAt").descending());

        SliceImpl<DirectMessage> directMessageSlice =
                new SliceImpl<>(directMessageList, pageRequest, false);

        Slice<DirectMessageResponseDto> directMessageResponseDtoSlice = directMessageSlice.map(direct ->
                DirectMessageResponseDto.builder()
                        .messageId(direct.getId())
                        .workSpaceId(direct.getWorkSpaceId())
                        .roomId(direct.getRoomId())
                        .senderId(direct.getSenderId())
                        .senderName(direct.getSenderName())
                        .content(direct.getContent())
                        .readCount(direct.getReadCount())
                        .isEdited(direct.getIsEdited())
                        .isDeleted(direct.getIsDeleted())
                        .messageType(direct.getMessageType())
                        .createdAt(direct.getCreatedAt())
                        .build());

        given(directMessageRepository.findDirectMessageByRoomIdOrderByCreatedAtDesc(anyString(), any()))
                .willReturn(directMessageSlice);

        //when
        Slice<DirectMessageResponseDto> directMessageResponseDtoList =
                directMessageService.directMessageSliceOrderByCreatedDate(directChannelRoomId, pageRequest);

        //then
        assertThat(directMessageResponseDtoList.getNumber())
                .isEqualTo(directMessageResponseDtoSlice.getNumber());
        assertThat(directMessageResponseDtoList.getSize())
                .isEqualTo(directMessageResponseDtoSlice.getSize());

    }
}