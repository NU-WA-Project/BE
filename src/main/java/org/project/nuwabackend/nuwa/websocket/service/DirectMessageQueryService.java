package org.project.nuwabackend.nuwa.websocket.service;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.nuwa.domain.channel.Direct;
import org.project.nuwabackend.nuwa.domain.mongo.DirectMessage;
import org.project.nuwabackend.nuwa.domain.workspace.WorkSpaceMember;
import org.project.nuwabackend.nuwa.websocket.dto.request.MessageUpdateRequestDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.MessageDeleteResponseDto;
import org.project.nuwabackend.nuwa.websocket.dto.request.MessageDeleteRequestDto;
import org.project.nuwabackend.nuwa.websocket.dto.response.MessageUpdateResponseDto;
import org.project.nuwabackend.global.exception.custom.NotFoundException;
import org.project.nuwabackend.nuwa.channel.repository.jpa.DirectChannelRepository;
import org.project.nuwabackend.nuwa.workspacemember.repository.WorkSpaceMemberRepository;
import org.project.nuwabackend.nuwa.auth.service.token.JwtUtil;
import org.project.nuwabackend.nuwa.file.service.FileService;
import org.project.nuwabackend.nuwa.websocket.type.MessageType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.project.nuwabackend.global.response.type.ErrorMessage.CHANNEL_NOT_FOUND;
import static org.project.nuwabackend.global.response.type.ErrorMessage.DIRECT_MESSAGE_DELETE_FAIL;
import static org.project.nuwabackend.global.response.type.ErrorMessage.DIRECT_MESSAGE_UPDATE_FAIL;
import static org.project.nuwabackend.global.response.type.ErrorMessage.WORK_SPACE_MEMBER_NOT_FOUND;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.DELETE;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.FILE;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.IMAGE;
import static org.project.nuwabackend.nuwa.websocket.type.MessageType.UPDATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectMessageQueryService {

    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final DirectChannelRepository directChannelRepository;
    private final MongoTemplate mongoTemplate;
    private final FileService fileService;
    private final JwtUtil jwtUtil;

    // 다이렉트 채널 읽지 않은 메세지 전부 읽음으로 변경 => 벌크 연산
    public void updateReadCountZero(String directChannelRoomId, String email) {
        log.info("채팅 전부 읽음으로 변경");

        Direct direct = directChannelRepository.findByRoomId(directChannelRoomId)
                .orElseThrow(() -> new NotFoundException(CHANNEL_NOT_FOUND));

        Long workSpaceId = direct.getWorkSpace().getId();

        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        // 보낸 사람이 아닌 메세지를 전부 읽음 처리
        Update update = new Update().set("direct_read_count", 0);
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").ne(senderId));
        mongoTemplate.updateMulti(query, update, DirectMessage.class);
    }

    // 읽지 않은 메세지 카운트
    public Long countUnReadMessage(String directChannelRoomId, String email, Long workSpaceId) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        // 접속한 멤버ID가 아닌 메세지 전부 카운트
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_read_count").is(1L)
                .and("direct_sender_id").ne(senderId));
        return mongoTemplate.count(query, DirectMessage.class);
    }

    // 내가 보낸 메세지 카운트
    public Long countManyMessageSenderId(String directChannelRoomId, String email, Long workSpaceId) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        // 해당 워크스페이스에 있는 메세지 중
        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").is(senderId));
        return mongoTemplate.count(query, DirectMessage.class);
    }

    // 내가 아닌 상대방이 보낸 메세지로 상대방 id 찾아오기
    public Long neSenderId(String directChannelRoomId, String email, Long workSpaceId) {
        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        Query query = new Query(Criteria.where("direct_room_id").is(directChannelRoomId)
                .and("direct_sender_id").ne(senderId));

        DirectMessage directMessage = mongoTemplate.findOne(query, DirectMessage.class);

//        if (directMessage != null) {
//            return directMessage.getSenderId();
//        } else {
//            throw new IllegalArgumentException(DIRECT_MESSAGE_NOT_FOUND.getMessage());
//        }
        return directMessage != null ? directMessage.getSenderId() : null;
    }

    // 메세지 수정
    public MessageUpdateResponseDto updateDirectMessage(String accessToken, MessageUpdateRequestDto messageUpdateRequestDto) {
        String email = jwtUtil.getEmail(accessToken);
        String id = messageUpdateRequestDto.id();
        String roomId = messageUpdateRequestDto.roomId();
        Long workSpaceId = messageUpdateRequestDto.workSpaceId();
        String content = messageUpdateRequestDto.content();
        MessageType messageType = messageUpdateRequestDto.messageType();
        boolean isEdited = messageType.equals(UPDATE);

        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId)
                .and("id").is(id)
                .and("direct_room_id").is(roomId)
                .and("direct_sender_id").is(senderId));

        Update update = new Update().set("direct_content", content).set("is_edited", isEdited);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, DirectMessage.class);

        if (updateResult.getMatchedCount() == 0 && !isEdited) {
            log.error("다이렉트 채널 메세지 수정 중 오류 = {}", DIRECT_MESSAGE_UPDATE_FAIL.getMessage());
            throw new IllegalArgumentException(DIRECT_MESSAGE_UPDATE_FAIL.getMessage());
        }

        return MessageUpdateResponseDto.builder()
                .id(id)
                .roomId(roomId)
                .workSpaceId(workSpaceId)
                .content(content)
                .messageType(messageType)
                .isEdited(isEdited)
                .build();
    }

    // 메세지 삭제
    public MessageDeleteResponseDto deleteDirectMessage(String accessToken, MessageDeleteRequestDto messageDeleteRequestDto) {
        String email = jwtUtil.getEmail(accessToken);
        String id = messageDeleteRequestDto.id();
        String roomId = messageDeleteRequestDto.roomId();
        Long workSpaceId = messageDeleteRequestDto.workSpaceId();
        MessageType messageType = messageDeleteRequestDto.messageType();
        boolean isDeleted = messageType.equals(DELETE);

        WorkSpaceMember sender = workSpaceMemberRepository.findByMemberEmailAndWorkSpaceId(email, workSpaceId)
                .orElseThrow(() -> new NotFoundException(WORK_SPACE_MEMBER_NOT_FOUND));

        Long senderId = sender.getId();

        String content = "삭제된 메세지입니다.";

        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId)
                .and("id").is(id)
                .and("direct_room_id").is(roomId)
                .and("direct_sender_id").is(senderId));

        DirectMessage directMessage = mongoTemplate.findOne(query, DirectMessage.class);

        if (directMessage != null && (directMessage.getMessageType().equals(IMAGE) || directMessage.getMessageType().equals(FILE))) {
            List<String> rawString = directMessage.getRawString();

            List<Long> idList = fileService.fileIdList(rawString);

            idList.forEach(fileService::deleteFile);
        }

        Update update = new Update().set("direct_content", content).set("is_deleted", isDeleted).set("raw_string", content);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, DirectMessage.class);

        if (updateResult.getMatchedCount() == 0) {
            log.error("다이렉트 채널 메세지 삭제 중 오류 = {}", DIRECT_MESSAGE_DELETE_FAIL.getMessage());
            throw new IllegalArgumentException(DIRECT_MESSAGE_DELETE_FAIL.getMessage());
        }

        return MessageDeleteResponseDto.builder()
                .id(id)
                .roomId(roomId)
                .workSpaceId(workSpaceId)
                .content(content)
                .isDeleted(isDeleted)
                .messageType(messageType)
                .build();
    }


    // 워크스페이스 ID로 관련 다이렉트 채팅 메세지 전부 삭제
    public void deleteDirectMessageWorkSpaceId(Long workSpaceId) {
        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId));

        mongoTemplate.remove(query, DirectMessage.class);
    }

    // 워크스페이스 ID와 Room ID 관련 다이렉트 메세지 전부 삭제
    public void deleteDirectMessageByWorkSpaceIdAndRoomId(Long workSpaceId, String roomId) {
        Query query = new Query(Criteria.where("workspace_id").is(workSpaceId)
                .and("direct_room_id").is(roomId));

        mongoTemplate.remove(query, DirectMessage.class);
    }
}
