package org.project.nuwabackend.nuwa.websocket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.domain.channel.Direct;
import org.project.nuwabackend.nuwa.domain.mongo.DirectMessage;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.websocket.dto.request.DirectMessageRequestDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.DirectMessageResponseDto;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.channel.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.websocket.repository.DirectMessageRepository;
import org.project.nuwabackend.nuwa.notification.service.NotificationService;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.channel.service.DirectChannelRedisService;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;
import org.project.nuwabackend.nuwa.notification.type.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.project.nuwabackend.global.response.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.ENTER;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.FILE;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.IMAGE;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.TEXT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectMessageService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final DirectChannelRepository directChannelRepository;
    private final DirectMessageRepository directMessageRepository;

    private final DirectChannelRedisService directChannelRedisService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private static final String PREFIX_URL = "/direct-chat/";

    // 메세지 저장
    @Transactional
    public DirectMessageResponseDto saveDirectMessage(DirectMessageResponseDto directMessageResponseDto) {
        log.info("메세지 저장");
        Long workSpaceId = directMessageResponseDto.getWorkSpaceId();
        String directChannelRoomId = directMessageResponseDto.getRoomId();
        Long senderId = directMessageResponseDto.getSenderId();
        String senderName = directMessageResponseDto.getSenderName();
        String directContent = directMessageResponseDto.getContent();
        List<String> rawString = directMessageResponseDto.getRawString();
        Long readCount = directMessageResponseDto.getReadCount();
        MessageType messageType = directMessageResponseDto.getMessageType();
        LocalDateTime createdAt = directMessageResponseDto.getCreatedAt();

        DirectMessage directMessage = DirectMessage.createDirectMessage(
                workSpaceId,
                directChannelRoomId,
                senderId,
                senderName,
                directContent,
                rawString,
                readCount,
                messageType,
                createdAt);

        DirectMessage saveDirectMessage = directMessageRepository.save(directMessage);
        directMessageResponseDto.setMessageId(saveDirectMessage.getId());
        directMessageResponseDto.setIsEdited(saveDirectMessage.getIsEdited());
        directMessageResponseDto.setIsDeleted(saveDirectMessage.getIsDeleted());

        return directMessageResponseDto;
    }

    // 저장된 메세지 가져오기 (Slice)
    public Slice<DirectMessageResponseDto> directMessageSliceOrderByCreatedDate(String directChannelRoomId, Pageable pageable) {
        log.info("저장된 메세지 가져오기");
        return directMessageRepository.findDirectMessageByRoomIdOrderByCreatedAtDesc(directChannelRoomId, pageable)
                .map(directMessage -> DirectMessageResponseDto.builder()
                        .messageId(directMessage.getId())
                        .workSpaceId(directMessage.getWorkSpaceId())
                        .roomId(directMessage.getRoomId())
                        .senderId(directMessage.getSenderId())
                        .senderName(directMessage.getSenderName())
                        .content(directMessage.getContent())
                        .rawString(directMessage.getRawString())
                        .readCount(directMessage.getReadCount())
                        .isEdited(directMessage.getIsEdited())
                        .isDeleted(directMessage.getIsDeleted())
                        .messageType(directMessage.getMessageType())
                        .createdAt(directMessage.getCreatedAt())
                        .build());
    }

    // 입장 메세지
    @Transactional
    public DirectMessageResponseDto enterMessage(String accessToken, String roomId) {
        log.info("입장 메세지");
        String email = jwtUtil.getEmail(accessToken);

        Direct direct = directChannelRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        Long workSpaceId = direct.getWorkSpace().getId();

        // 메세지 보낸 사람
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        return DirectMessageResponseDto.builder()
                .workSpaceId(workSpaceId)
                .roomId(roomId)
                .senderId(sender.getId())
                .senderName(sender.getName())
                .content(sender.getName() + "님이 입장했습니다.")
                .readCount(0L)
                .messageType(ENTER)
                .build();
    }

    // 메세지 보내기
    @Transactional
    public DirectMessageResponseDto sendMessage(String accessToken, DirectMessageRequestDto directMessageRequestDto) {

        DirectMessageResponseDto directMessageResponseDto;

        log.info("메세지 보내기");
        try {
            String email = jwtUtil.getEmail(accessToken);
            Long workSpaceId = directMessageRequestDto.workSpaceId();
            String directChannelRoomId = directMessageRequestDto.roomId();
            String directChannelContent = directMessageRequestDto.content();
            List<String> rawString = directMessageRequestDto.rawString();
            Long receiverId = directMessageRequestDto.receiverId();
            MessageType messageType = directMessageRequestDto.messageType();
            String notificationContent;

            if (messageType.equals(IMAGE)) {
                notificationContent = "사진";
            } else if (messageType.equals(FILE)) {
                notificationContent = "파일";
            } else if (messageType.equals(TEXT)) {
                notificationContent = rawString.get(0);
            } else {
                notificationContent = "";
            }

            // 메세지 보낸 사람
            WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                    .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

            Long senderId = sender.getId();
            String senderName = sender.getName();

            // 메세지 받는 사람 (알림용)
            WorkSpaceMember receiver = workSpaceMemberRepository.findById(receiverId)
                    .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

            boolean isAllConnected = directChannelRedisService.isAllConnected(directChannelRoomId);

            Long readCount = isAllConnected ? 0L : 1L;

            // 채팅을 읽지 않았을 때 알림을 전송
            // 읽었을 땐 알림을 보내지 않습니다.
            if (!readCount.equals(0L)) {
                log.info("알림 전송");
                notificationService.send(
                        notificationContent,
                        createDirectUrl(directChannelRoomId),
                        NotificationType.DIRECT,
                        sender,
                        receiver);
            }

            directMessageResponseDto = DirectMessageResponseDto.builder()
                    .workSpaceId(workSpaceId)
                    .roomId(directChannelRoomId)
                    .senderId(senderId)
                    .senderName(senderName)
                    .content(directChannelContent)
                    .rawString(rawString)
                    .readCount(readCount)
                    .messageType(messageType)
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("메세지 전송 중 오류 발생 = {}", e.getMessage());
            throw new IllegalStateException("메세지 전송 중 오류가 발생 했습니다. 오류 내용 = " + e.getMessage());
        }
        return directMessageResponseDto;
    }

    private String createDirectUrl(String directChannelRoomId) {
        return PREFIX_URL + directChannelRoomId;
    }
}



