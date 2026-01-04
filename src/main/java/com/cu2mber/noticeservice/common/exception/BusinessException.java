package com.cu2mber.noticeservice.common.exception;

/**
 * 애플리케이션 비즈니스 로직 수행 중 발생하는 모든 예외의 최상위 추상 클래스
 * <p>
 * 이 클래스는 시스템적인 런타임 오류와 구분되는, 비즈니스 규칙 위반 상황을 처리하기 위해 설계되었습니다.
 * 각 예외 상황에 맞는 HTTP 상태 코드({@code statusCode})를 내부적으로 보유하여,
 * {@link GlobalExceptionHandler}에서 일관된 형태로 응답을 생성할 수 있도록 돕습니다.
 * </p>
 * * <p>새로운 비즈니스 예외를 정의할 때는 이 클래스를 상속받아 구현해야 합니다.</p>
 */
public abstract class BusinessException extends RuntimeException {
    private final int statusCode;

    /**
     * BusinessException 생성자
     *
     * @param message    에러 메시지
     * @param statusCode HTTP 상태 코드
     */
    public BusinessException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * 설정된 HTTP 상태 코드 반환
     *
     * @return HTTP 상태 코드
     */
    public int getStatusCode() {
        return statusCode;
    }
}
