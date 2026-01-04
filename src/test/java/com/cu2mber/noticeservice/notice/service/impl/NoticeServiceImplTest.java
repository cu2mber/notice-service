package com.cu2mber.noticeservice.notice.service.impl;

import com.cu2mber.noticeservice.common.exception.BusinessException;
import com.cu2mber.noticeservice.common.exception.custom.AdminForbiddenException;
import com.cu2mber.noticeservice.common.exception.custom.NoticeNotFoundException;
import com.cu2mber.noticeservice.notice.domain.Notice;
import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;
import com.cu2mber.noticeservice.notice.repository.NoticeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 공지사항 서비스(NoticeServiceImpl)의 단위 테스트 클래스
 * Mockito를 사용하여 Repository 의존성을 분리하고 비즈니스 로직을 검증합니다.
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

    @Mock
    NoticeRepository noticeRepository;

    @InjectMocks
    NoticeServiceImpl noticeService;

    @Test
    @DisplayName("관리자 권한으로 공지사항을 등록하면 성공")
    void createNotice_Success() {
        NoticeRequest request = new NoticeRequest("제목", "내용", true);
        String role = "ROLE_ADMIN";
        Long memberNo = 1L;

        Notice savedNotice = Notice.builder()
                .noticeTitle("제목")
                .noticeContent("내용")
                .build();

        ReflectionTestUtils.setField(savedNotice, "noticeNo", 100L);

        when(noticeRepository.save(any(Notice.class))).thenReturn(savedNotice);

        NoticeResponse noticeResponse = noticeService.createNotice(request, role, memberNo);

        log.info("테스트 검증 - 결과 NoticeNo: {}", noticeResponse);
        assertEquals(100L, noticeResponse.noticeNo());
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }

    @Test
    @DisplayName("관리자가 아닌 사용자가 등록을 시도하면 예외 발생")
    void createNotice_Fail_Role() {
        NoticeRequest request = new NoticeRequest("제목", "내용", true);
        String role = "ROLE_USER";
        Long memberNo = 1L;

        BusinessException exception = assertThrows(AdminForbiddenException.class, () -> {
            noticeService.createNotice(request, role, memberNo);
        });

        assertEquals("관리자만 접근할 수 있는 권한입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("관리자가 공지사항 내용을 수정하면 성공")
    void updateNotice_Success() {
        Long noticeNo = 100L;
        NoticeRequest updateRequest = new NoticeRequest("수정제목", "수정내용", false);
        Notice existingNotice = Notice.builder()
                .noticeTitle("기존제목").noticeContent("기존내용").isFixed(true).build();

        when(noticeRepository.findById(noticeNo)).thenReturn(Optional.of(existingNotice));

        NoticeResponse response = noticeService.updateNotice(noticeNo, updateRequest, "ROLE_ADMIN");

        assertEquals("수정제목", response.noticeTitle());
        assertEquals("수정내용", response.noticeContent());
        assertFalse(response.isFixed());
    }

    @Test
    @DisplayName("관리자가 공지사항을 삭제하면 성공")
    void deleteNotice_Success() {
        Long noticeNo = 100L;
        String role = "ROLE_ADMIN";

        Notice notice = Notice.builder().build();
        ReflectionTestUtils.setField(notice, "noticeNo", noticeNo);

        when(noticeRepository.existsById(noticeNo)).thenReturn(true);

        noticeService.deleteNotice(noticeNo, role);

        verify(noticeRepository, times(1)).deleteById(noticeNo);
    }

    @Test
    @DisplayName("존재하지 않는 공지사항 삭제 시 예외 발생")
    void deleteNotice_Fail_NotFound() {
        Long noticeNo = 999L;
        String role = "ROLE_ADMIN";

        when(noticeRepository.existsById(noticeNo)).thenReturn(false);

        BusinessException exception = assertThrows(NoticeNotFoundException.class, () -> {
            noticeService.deleteNotice(noticeNo, role);
        });

        assertEquals("존재하지 않는 공지사항입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("전체 공지사항 목록 조회 - 키워드 없이 전체 페이지 반환")
    void getAllNotices_Success() {
        Long memberNo = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = null;

        Notice notice1 = Notice.builder()
                .noticeTitle("공지사항 1")
                .noticeContent("내용 1")
                .memberNo(memberNo)
                .isFixed(true)
                .build();
        ReflectionTestUtils.setField(notice1, "noticeNo", 1L);
        ReflectionTestUtils.setField(notice1, "createdAt", LocalDateTime.now());

        Notice notice2 = Notice.builder()
                .noticeTitle("공지사항 2")
                .noticeContent("내용 2")
                .memberNo(memberNo)
                .isFixed(false)
                .build();
        ReflectionTestUtils.setField(notice2, "noticeNo", 2L);
        ReflectionTestUtils.setField(notice2, "createdAt", LocalDateTime.now().minusDays(1));

        List<Notice> mockList = Arrays.asList(notice1, notice2);
        Page<Notice> mockPage = new PageImpl<>(mockList, pageable, mockList.size());

        when(noticeRepository.findAllNoticesWithPaging(any(Pageable.class))).thenReturn(mockPage);

        Page<NoticeResponse> result = noticeService.getAllNotices(0, 10, keyword);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("공지사항 1", result.getContent().get(0).noticeTitle());

        verify(noticeRepository, times(1)).findAllNoticesWithPaging(any(Pageable.class));
        verify(noticeRepository, never()).findByNoticeTitleContaining(anyString(), any(Pageable.class));
    }
    @Test
    @DisplayName("공지사항 키워드 검색 조회 - 검색어가 포함된 결과만 반환")
    void getNoticeList_WithKeyword_Success() {
        Long memberNo = 1L;
        String keyword = "안내";
        Pageable pageable = PageRequest.of(0, 10);

        Notice notice1 = Notice.builder()
                .noticeTitle("[점검] 서버 점검 안내")
                .noticeContent("내용 1")
                .memberNo(memberNo)
                .isFixed(true)
                .build();
        ReflectionTestUtils.setField(notice1, "noticeNo", 1L);
        ReflectionTestUtils.setField(notice1, "createdAt", LocalDateTime.now());

        List<Notice> mockList = List.of(notice1);
        Page<Notice> mockPage = new PageImpl<>(mockList, pageable, mockList.size());

        when(noticeRepository.findByNoticeTitleContaining(eq(keyword), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<NoticeResponse> result = noticeService.getAllNotices(0, 10, keyword);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertTrue(result.getContent().get(0).noticeTitle().contains(keyword));

        log.info("테스트 성공 : 키워드 검색 필터링 확인");

        verify(noticeRepository, times(1)).findByNoticeTitleContaining(eq(keyword), any(Pageable.class));
        verify(noticeRepository, never()).findAllNoticesWithPaging(any(Pageable.class));
    }

    @Test
    @DisplayName("공지사항이 하나도 없을 때 빈 페이지 반환")
    void getAllNotices_Empty() {
        Pageable pageable = PageRequest.of(0, 10);
        String keyword = null;

        when(noticeRepository.findAllNoticesWithPaging(pageable))
                .thenReturn(new PageImpl<>(List.of()));

        Page<NoticeResponse> result = noticeService.getAllNotices(0, 10, keyword);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());

        log.info("테스트 성공 : 빈 페이지 반환 확인");
        verify(noticeRepository, times(1)).findAllNoticesWithPaging(pageable);
    }
}