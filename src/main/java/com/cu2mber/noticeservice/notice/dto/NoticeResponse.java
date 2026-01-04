package com.cu2mber.noticeservice.notice.dto;

import com.cu2mber.noticeservice.notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 공지사항 정보를 반환하는 응답 객체입니다.
 */
public record NoticeResponse(
        @Schema(description = "공지사항 번호")
        Long noticeNo,

        @Schema(description = "공지사항 제목")
        String noticeTitle,

        @Schema(description = "공지사항 내용")
        String noticeContent,

        @Schema(description = "상단 고정 여부")
        boolean isFixed,

        @Schema(description = "작성자 이름")
        String memberName,

        @Schema(description = "작성일")
        LocalDateTime createdAt
) {
    /** 엔티티를 응답 DTO로 변환 */
    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(
                notice.getNoticeNo(),
                notice.getNoticeTitle(),
                notice.getNoticeContent(),
                notice.isFixed(),
                "관리자",
                notice.getCreatedAt()
        );
    }
}