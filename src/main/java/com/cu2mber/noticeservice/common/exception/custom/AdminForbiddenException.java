package com.cu2mber.noticeservice.common.exception.custom;

import com.cu2mber.noticeservice.common.exception.BusinessException;

/**
 * 관리자 권한이 없는 사용자가 접근을 시도할 때 발생하는 비즈니스 예외 클래스
 * <p>
 * 이 예외는 {@link BusinessException}을 상속받으며,
 * 발생 시 클라이언트에게 HTTP 403 (Forbidden) 상태 코드를 반환하도록 설계되었습니다.
 * </p>
 */
public class AdminForbiddenException extends BusinessException {
    private static final String DEFAULT_MESSAGE = "관리자만 접근할 수 있는 권한입니다.";

    public AdminForbiddenException() {
        super(DEFAULT_MESSAGE, 403);
    }
}