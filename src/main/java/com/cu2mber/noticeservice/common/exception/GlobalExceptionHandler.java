package com.cu2mber.noticeservice.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

/**
 * 애플리케이션 전역에서 발생하는 예외를 중앙 집중식으로 처리하는 핸들러 클래스
 * <p>본 클래스는 계층 분리 원칙에 따라 다음과 같이 예외를 구분하여 처리합니다:</p>
 * <ul>
 * <li><b>RuntimeException :</b> 위에서 처리되지 않은 예상치 못한 시스템 오류. 보안을 위해 상세 내용을 숨기고 500(Internal Server Error)으로 응답합니다.</li>
 * 각 예외 객체가 들고 있는 상태 코드에 따라 응답합니다.</li>
 * <li><b>MethodArgumentNotValidException :</b> {@code @Valid}를 통한 입력값 검증 실패 시 발생하며, 400(Bad Request)으로 응답합니다.</li>
 * <li><b>BusinessException :</b> 서비스 로직에서 의도적으로 던지는 비즈니스 예외 (공지사항 미존재, 권한 부족 등)
 * </ul>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 예상치 못한 서버 내부 오류 처리
     * <p>비즈니스 로직 외의 런타임 예외(NPE, DB 에러 등)를 처리하며, 로그에는 상세 내용을 남기되 클라이언트에게는 일반적인 메시지만 노출합니다.</p>
     *
     * @param e 런타임 예외 객체
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException 발생: {}", e.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류가 발생했습니다.",
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * 데이터 유효성 검증 실패 예외 처리
     * <p>DTO의 {@code @NotBlank}, {@code @Size} 등 제약 조건 위반 시 발생합니다.</p>
     *
     * @param e 검증 예외 객체
     * @return 400 Bad Request와 첫 번째 검증 오류 메시지
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            message,
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 커스텀 비즈니스 예외 처리
     * <p>BusinessException을 상속받은 모든 하위 예외(AdminForbiddenException 등)를 일괄 처리합니다.</p>
     *
     * @param e BusinessException 또는 그 하위 클래스 인스턴스
     * @return 예외 객체에 정의된 상태 코드와 메시지를 담은 ResponseEntity
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorResponse(
                        e.getStatusCode(),
                        e.getMessage(),
                        LocalDateTime.now()
                ));
    }
}