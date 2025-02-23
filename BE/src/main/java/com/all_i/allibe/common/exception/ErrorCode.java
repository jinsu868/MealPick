package com.all_i.allibe.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(1000, "유효하지 않은 요청입니다."),
    DUPLICATE_FOLLOW_REQUEST(1001, "중복 팔로우 요청은 불가합니다."),
    MEMBER_NOT_FOUND(1002, "요청 ID에 해당하는 유저가 존재하지 않습니다."),
    FOLLOW_REQUEST_NOT_FOUND(1003, "요청 ID에 해당하는 팔로우 요청이 존재하지 않습니다."),
    FOLLOW_HISTORY_NOT_FOUND(1004, "요청의 기록을 찾을 수 없습니다."),
    ALREADY_FOLLOWED(1008, "이미 팔로우 되어 요청 오류가 발생했습니다."),
    ALREADY_REQUESTED(1009, "이미 팔로우 요청 되어 오류가 발생했습니다."),

    UNABLE_TO_GET_USER_INFO(2001, "소셜 로그인 공급자로부터 유저 정보를 받아올 수 없습니다."),
    UNABLE_TO_GET_ACCESS_TOKEN(2002, "소셜 로그인 공급자로부터 인증 토큰을 받아올 수 없습니다."),
    NOT_SOCIAL_MEMBER(2003, "소셜 로그인 유저가 아닙니다."),
    NOT_NONE_SOCIAL_MEMBER(2004, "일반 로그인 유저가 아닙니다."),

    UNAUTHORIZED_ACCESS(3000, "접근할 수 없는 리소스입니다."),
    INVALID_REFRESH_TOKEN(3001, "유효하지 않은 Refresh Token입니다."),
    FAILED_TO_VALIDATE_TOKEN(3002, "토큰 검증에 실패했습니다."),
    INVALID_ACCESS_TOKEN(3003, "유효하지 않은 Access Token입니다."),

    VALIDATION_FAIL(4000, "유효하지 않은 형식입니다."),
    INTERNAL_SERVER_ERROR(4001, "Internal Server Error"),
    SELF_FOLLOW_NOT_ALLOWED(4002, "자기 자신에 대한 팔로우는 허용되지 않습니다."),
    INVALID_FOLLOW_REQUEST(4003, "팔로우 요청처리에 실패했습니다."),
    INVALID_STATUS_TRANSITION(4004, "유효하지 않은 상태입니다."),

    POST_NOT_FOUND(5000, "요청 ID에 해당하는 게시글이 존재하지 않습니다"),
    AT_LEAST_ONE_POST_REQUIRED(5001, "별칭을 생성하려면 적어도 하나의 게시글이 필요합니다."),
    DUPLICATE_TAG(5001, "Tag가 중복되었습니다."),
    NOT_MY_POST(5002, "게시물 작성자가 아닙니다."),
    DUPLICATE_POST_LIKE(5003, "게시글에 중복해서 좋아요를 할 수 없습니다."),
    POST_LIKE_NOT_FOUND(5004, "해당 게시글 좋아요를 찾을 수 없습니다."),
    AUTHOR_NOT_EXIST(5005, "작성자가 존재하지 않는 게시글입니다."),
    INVALID_DATA(5050, "유효하지 않은 형식입니다."),
    INCORRECT_MEAL_TIME(5006, "요청 MealTime이 현재 MealTime과 맞지 않습니다."),


    COMMENT_NOT_FOUND(6000, "해당 댓글을 찾을 수 없습니다."),
    NO_MORE_COMMENT(6001, "대댓글에 댓글을 추가할 수 없습니다."),
    NOT_MY_COMMENT(6002, "댓글 작성자가 아닙니다."),
    DUPLICATE_COMMENT_LIKE(6003, "댓글에 중복해서 좋아요를 할 수 없습니다."),
    COMMENT_LIKE_NOT_FOUND(6004, "해당 댓글 좋아요를 찾을 수 없습니다."),

    SCRAB_NOT_FOUND(7000, "해당 스크랩을 찾을 수 없습니다."),
    DUPLICATE_SCRAB(7001, "이미 존재하는 스크랩입니다."),

    CHAT_ROOM_NOT_FOUND(8000, "해당 채팅방을 찾을 수 없습니다."),
    MEMBER_NOT_IN_CHAT_ROOM(8001, "채팅방에 존재하지 않는 유저입니다."),
    CHATROOM_ALREADY_EXISTS(8002, "이미 두 유저 사이에 1:1채팅방이 존재합니다."),
    NO_ADDRESS_IN_REDIS(8003, "Redis에 상대방 ServerIP가 존재하지 않습니다."),
    NO_CLIENT_IP(8004, "주어진 ID에 대한 ClientIP가 존재하지 않습니다.")
    ;

    private final int code;
    private final String message;
}
