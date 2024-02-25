package org.project.nuwabackend.service.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.nuwabackend.domain.redis.DirectChannelRedis;
import org.project.nuwabackend.global.exception.NotFoundException;
import org.project.nuwabackend.repository.redis.DirectChannelRedisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.project.nuwabackend.global.type.ErrorMessage.REDIS_DIRECT_CHANNEL_AND_EMAIL_NOT_FOUND_INFO;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectChannelRedisService {

    private final DirectChannelRedisRepository directChannelRedisRepository;


    // Redis에 채널 입장 정보 저장
    @Transactional
    public void saveDirectChannelMemberInfo(String directChannelRoomId, String email) {
        log.info("채널 입장 정보 저장");
        DirectChannelRedis directChannelInfo =
                DirectChannelRedis.createDirectChannelRedis(directChannelRoomId, email);

        directChannelRedisRepository.save(directChannelInfo);
    }

    // Redis에 채널 입장 정보 삭제
    @Transactional
    public void deleteDirectChannelMemberInfo(String directChannelRoomId, String email) {
        DirectChannelRedis directChannelRedis = directChannelRedisRepository.findByDirectRoomIdAndEmail(directChannelRoomId, email)
                .orElseThrow(() -> new NotFoundException(REDIS_DIRECT_CHANNEL_AND_EMAIL_NOT_FOUND_INFO));

        directChannelRedisRepository.delete(directChannelRedis);
    }

    // 채팅방 인원이 2명인지 확인 => 다이렉트 메세지를 보냈을 때 바로 읽음 처리를 하기 위한 메소드
    public boolean isAllConnected(String directChannelRoomId) {
        List<DirectChannelRedis> connectList = directChannelRedisRepository.findByDirectRoomId(directChannelRoomId);
        return connectList.size() == 2;
    }

    // 채팅방 인원이 1명인지 확인 => 채팅방 연결시 현재 인원이 존재 하는지 확인을 위한 메소드
    public boolean isConnected(String directChannelRoomId) {
        List<DirectChannelRedis> connectList = directChannelRedisRepository.findByDirectRoomId(directChannelRoomId);
        return connectList.size() == 1;
    }
}