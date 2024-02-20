package org.project.nuwabackend.dto.message.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DirectMessageResponseDto(Long workSpaceId, String roomId, Long senderId, String senderName, String content, Long readCount, LocalDateTime createdAt) {

}
