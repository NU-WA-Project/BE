package org.project.nuwabackend.dto.channel.request;

import java.util.List;

public record VoiceChannelJoinMemberRequest(Long voiceChannelId, List<String> joinMemberNameList) {
}