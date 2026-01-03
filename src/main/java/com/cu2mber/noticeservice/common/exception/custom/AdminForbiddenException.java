package com.cu2mber.noticeservice.common.exception.custom;

public class AdminForbiddenException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "관리자만 접근할 수 있는 권한입니다.";

    public AdminForbiddenException() {
        super(DEFAULT_MESSAGE);
    }
}