package com.cu2mber.noticeservice.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공지사항 생성을 위한 요청 DTO 클래스입니다.
 * * <p>필드 설명:</p>
 * <ul>
 * <li>isFixed: 클라이언트의 명시적 요청 여부를 확인하기 위해 래퍼 클래스(Boolean)를 사용합니다.
 * 값이 누락될 경우 기본값인 false로 처리되지만, 필요한 경우 @NotNull을 통해
 * 클라이언트에게 필수 입력을 강제할 수 있는 유연성을 제공합니다.</li>
 * </ul>
 */
@Getter
@NoArgsConstructor
public class NoticeRequest {
    @Schema(description = "공지사항 제목")
    @NotBlank(message = "제목은 비어있을 수 없습니다.")
    private String noticeTitle;

    @Schema(description = "공지사항 내용")
    @NotBlank(message = "내용은 비어있을 수 없습니다.")
    private String noticeContent;

    @Schema(description = "상단 고정 여부 (기본값: false)")
    private Boolean isFixed = false;
}
