package com.qbank.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 인증
    UNAUTHORIZED("A001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),

    // 사용자
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 질문
    QUESTION_NOT_FOUND("Q001", "질문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    QUESTION_ACCESS_DENIED("Q002", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 태그
    TAG_NOT_FOUND("T001", "존재하지 않는 태그가 포함되어 있습니다.", HttpStatus.BAD_REQUEST),

    // 팔로우
    FOLLOW_SELF("F001", "자기 자신을 팔로우할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 북마크
    BOOKMARK_ALREADY_EXISTS("B001", "이미 북마크한 질문입니다.", HttpStatus.CONFLICT),
    BOOKMARK_NOT_FOUND("B002", "북마크를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 복습
    REVIEW_STATUS_INVALID("R001", "유효하지 않은 복습 상태입니다.", HttpStatus.BAD_REQUEST),

    // 답변
    ANSWER_NOT_FOUND("AN001", "답변을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ANSWER_ALREADY_EXISTS("AN002", "이미 답변이 존재합니다. 수정은 PUT을 사용하세요.", HttpStatus.CONFLICT),

    // 그룹
    GROUP_NOT_FOUND("GR001", "그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    GROUP_ACCESS_DENIED("GR002", "그룹에 대한 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
