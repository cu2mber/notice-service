package com.cu2mber.noticeservice.notice.service.impl;

import com.cu2mber.noticeservice.member.domain.Member;
import com.cu2mber.noticeservice.member.repository.MemberRepository;
import com.cu2mber.noticeservice.notice.domain.Notice;
import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;
import com.cu2mber.noticeservice.notice.repository.NoticeRepository;
import com.cu2mber.noticeservice.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
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
    private final MemberRepository memberRepository;

    /**
     * 관리자 권한 확인 후 공지사항 등록
     * @throws RuntimeException ROLE_ADMIN이 아닐 경우 발생
     */
    @Override
    @Transactional
    public Long createNotice(NoticeRequest request, String role, Long memberNo) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new RuntimeException("관리자만 공지사항을 등록할 수 있습니다.");
        }

        Member admin = memberRepository.getReferenceById(memberNo);

        Notice notice = Notice.builder()
                .member(admin)
                .noticeTitle(request.getNoticeTitle())
                .noticeContent(request.getNoticeContent())
                .isFixed(request.getIsFixed())
                .build();

        return noticeRepository.save(notice).getNoticeNo();
    }

    /**
     * 관리자 권한 확인 후 공지사항 삭제
     * @throws RuntimeException ROLE_ADMIN이 아닐 경우 발생
     */
    @Override
    @Transactional
    public void deleteNotice(Long noticeNo, String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            throw new RuntimeException("관리자만 공지사항을 삭제할 수 있습니다.");
        }

        noticeRepository.deleteById(noticeNo);
    }

    /**
     * 고정글 우선, 최신순 정렬로 전체 공지사항 반환
     */
    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponse> getAllNotices() {
        return noticeRepository.findAllByOrderByIsFixedDescCreatedAtDesc().stream()
                .map(NoticeResponse::from)
                .collect(Collectors.toList());
    }
}
