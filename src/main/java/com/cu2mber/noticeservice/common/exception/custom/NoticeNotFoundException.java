package com.cu2mber.noticeservice.common.exception.custom;

import com.cu2mber.noticeservice.common.exception.BusinessException;

/**
 * 요청한 공지사항을 찾을 수 없을 때 발생하는 비즈니스 예외 클래스
 * <p>
 * 이 예외는 {@link BusinessException}을 상속받으며,
 * 발생 시 클라이언트에게 HTTP 404 (Not Found) 상태 코드를 반환하도록 설계되었습니다.
 * </p>
 */
public class NoticeNotFoundException extends BusinessException {
    private static final String DEFAULT_MESSAGE = "존재하지 않는 공지사항입니다.";

    public NoticeNotFoundException() {
        super(DEFAULT_MESSAGE, 404);
    }
}