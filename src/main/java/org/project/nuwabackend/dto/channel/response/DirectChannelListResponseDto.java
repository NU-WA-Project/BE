package org.project.nuwabackend.dto.channel.response;

import lombok.Builder;

import java.util.List;

@Builder
public record DirectChannelListResponseDto(List<DirectChannelResponseDto> directChannelResponseDtoList,
                                           boolean hasNext,
                                           int currentPage,
                                           int pageSize) {
}