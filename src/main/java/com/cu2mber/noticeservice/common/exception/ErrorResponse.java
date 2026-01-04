package com.cu2mber.noticeservice.common.exception;

import java.time.LocalDateTime;

/**
 * 애플리케이션 전역에서 발생하는 예외 정보를 담는 응답 객체
 * 주로 @RestControllerAdvice에서 예외를 캐치하여 사용자에게 일관된 에러 형식을 응답할 때 사용됩니다.
 * @param status    HTTP 상태 코드
 * @param message   예외 메시지
 * @param timestamp 예외 발생 시간
 */
public record ErrorResponse(
    int status,
    String message,
    LocalDateTime timestamp
) {}