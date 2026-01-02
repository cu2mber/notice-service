package com.cu2mber.noticeservice.notice.service;

import com.cu2mber.noticeservice.member.domain.Member;
import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;

import java.util.List;

/**
 * 공지사항 관리를 위한 서비스 인터페이스
 */
public interface NoticeService {
    /** 공지사항 신규 등록 */
    NoticeResponse createNotice(NoticeRequest request, String role, Long memberNo);

    /** 공지사항 수정 */
    NoticeResponse updateNotice(Long noticeNo, NoticeRequest request, String role);

    /** 특정 공지사항 삭제 */
    void deleteNotice(Long noticeNo, String role);

    /** 공지사항 조회 */
    NoticeResponse getNotice(Long noticeNo);

    /** 전체 공지사항 목록 조회 (고정글 우선 정렬) */
    List<NoticeResponse> getAllNotices();
}
