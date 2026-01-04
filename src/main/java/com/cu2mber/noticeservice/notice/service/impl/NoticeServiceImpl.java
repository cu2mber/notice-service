package com.cu2mber.noticeservice.notice.service.impl;

import com.cu2mber.noticeservice.common.exception.custom.AdminForbiddenException;
import com.cu2mber.noticeservice.common.exception.custom.NoticeNotFoundException;
import com.cu2mber.noticeservice.notice.domain.Notice;
import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;
import com.cu2mber.noticeservice.notice.repository.NoticeRepository;
import com.cu2mber.noticeservice.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * NoticeService 실체 구현체 : 관리자 권한 검증 및 데이터 영속화 담당
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 관리자 권한 확인 후 공지사항 등록
     * @throws RuntimeException ROLE_ADMIN이 아닐 경우 발생
     */
    @Override
    @Transactional
    public NoticeResponse createNotice(NoticeRequest request, String role, Long memberNo) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new AdminForbiddenException();
        }

        Notice notice = Notice.builder()
                .memberNo(memberNo)
                .noticeTitle(request.getNoticeTitle())
                .noticeContent(request.getNoticeContent())
                .isFixed(request.getIsFixed())
                .build();

        Notice savedNotice = noticeRepository.save(notice);

        return NoticeResponse.from(savedNotice);
    }

    @Override
    @Transactional
    public NoticeResponse updateNotice(Long noticeNo, NoticeRequest request, String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new AdminForbiddenException();
        }

        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new NoticeNotFoundException());

        notice.update(
                request.getNoticeTitle(),
                request.getNoticeContent(),
                request.getIsFixed()
        );

        return NoticeResponse.from(notice);
    }

    /**
     * 관리자 권한 확인 후 공지사항 삭제
     * @throws RuntimeException ROLE_ADMIN이 아닐 경우 발생
     */
    @Override
    @Transactional
    public void deleteNotice(Long noticeNo, String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new AdminForbiddenException();
        }

        if (!noticeRepository.existsById(noticeNo)) {
            throw new NoticeNotFoundException();
        }

        noticeRepository.deleteById(noticeNo);
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long noticeNo) {

        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new NoticeNotFoundException());

        return NoticeResponse.from(notice);
    }

    /**
     * 고정글 우선, 최신순 정렬로 전체 공지사항 반환
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponse> getAllNotices(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Notice> noticePage;
        if (keyword != null && !keyword.isBlank()) {
            noticePage = noticeRepository.findByNoticeTitleContaining(keyword, pageable);
        } else {
            noticePage = noticeRepository.findAllNoticesWithPaging(pageable);
        }

        return noticePage.map(NoticeResponse::from);
    }

}
