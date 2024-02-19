package org.project.nuwabackend.global.type;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    DUPLICATE_NICKNAME("닉네임 중복입니다."),
    DUPLICATE_EMAIL("이메일 중복입니다."),
    JWT_EXPIRED("토큰이 만료되었습니다."),
    JWT_NOT_NORMAL_TOKEN("정상적인 토큰이 아닙니다."),
    EMAIL_NOT_FOUND_ID("존재하지 않는 아이디 입니다."),
    LOGIN_EMAIL_OR_PASSWORD_INACCURATE("아이디 또는 비밀번호가 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND("이메일로 리프레쉬 토큰 값을 찾을 수 없습니다."),
    OAUTH_PROVIDER_NOT_FOUND("소셜로그인 제공자를 찾을 수 없습니다."),
    OAUTH_ROLE_NOT_FOUND("권한 정보가 존재하지 않습니다."),
    EMAIL_NOT_FOUND_MEMBER("해당 유저가 존재하지 않습니다."),
    WORK_SPACE_NOT_FOUND("워크스페이스가 존재하지 않습니다."),
    WORK_SPACE_MEMBER_NOT_FOUND("해당 워크스페이스에 멤버가 존재하지 않습니다."),
    DIRECT_CHANNEL_NOT_FOUND("다이렉트 채널을 해당 멤버들로 찾을 수 없습니다."),
    REDIS_DIRECT_CHANNEL_AND_EMAIL_NOT_FOUND_INFO("레디스에 해당 정보가 존재하지 않습니다."),
    MEMBER_ID_NOT_FOUND("멤버가 존재하지 않습니다."),
    CHANNEL_NOT_FOUND("채널이 존재하지 않습니다."),
    FILE_EXTENSION_NOT_FOUND("파일의 확장자가 존재하지 않습니다."),
    FILE_EXTENSION_NOT_APPLY("파일의 확장자를 지원하지 않습니다."),
    EMAIL_NOT_FOUND("이메일 전송에 실패했습니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
