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

/**
 * 공지사항 관리를 위한 서비스 구현체
 * <p>
 * 관리자 권한 검증을 포함한 비즈니스 로직을 처리하며,
 * 데이터 접근 계층(Repository)과의 상호작용을 통해 공지사항 데이터를 관리합니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 새로운 공지사항을 등록
     *
     * @param request  등록할 공지사항 정보
     * @param role     요청자의 권한 (ROLE_ADMIN 필요)
     * @param memberNo 작성자 식별 번호
     * @return 등록된 공지사항 상세 정보
     * @throws AdminForbiddenException 관리자 권한이 없는 경우 발생
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

    /**
     * 기존 공지사항 수정
     *
     * @param noticeNo 수정할 공지사항 번호
     * @param request  수정할 내용 정보
     * @param role     요청자의 권한 (ROLE_ADMIN 필요)
     * @return 수정된 공지사항 상세 정보
     * @throws AdminForbiddenException 관리자 권한이 없는 경우 발생
     * @throws NoticeNotFoundException 해당 번호의 공지사항이 존재하지 않는 경우 발생
     */
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
     * 특정 공지사항 삭제
     *
     * @param noticeNo 삭제할 공지사항 번호
     * @param role     요청자의 권한 (ROLE_ADMIN 필요)
     * @throws AdminForbiddenException 관리자 권한이 없는 경우 발생
     * @throws NoticeNotFoundException 해당 번호의 공지사항이 존재하지 않는 경우 발생
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

    /**
     * 단일 공지사항 상세 정보를 조회
     *
     * @param noticeNo 조회할 공지사항 번호
     * @return 공지사항 상세 정보
     * @throws NoticeNotFoundException 해당 번호의 공지사항이 존재하지 않는 경우 발생
     */
    @Override
    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long noticeNo) {

        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new NoticeNotFoundException());

        return NoticeResponse.from(notice);
    }

    /**
     * 공지사항 목록을 페이징하여 조회
     * <p>키워드가 제공될 경우 제목 검색을 수행하며, 고정글이 우선적으로 정렬됩니다.</p>
     *
     * @param page    페이지 번호 (0부터 시작)
     * @param size    한 페이지당 노출할 개수
     * @param keyword 검색어 (선택 사항)
     * @return 페이징 처리된 공지사항 목록
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
