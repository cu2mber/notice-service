package com.cu2mber.noticeservice.notice.service.impl;

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
import org.springframework.test.util.ReflectionTestUtils;

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

        RuntimeException exception = assertThrows(AdminForbiddenException.class, () -> {
            noticeService.createNotice(request, role, memberNo);
        });

        assertEquals("관리자만 공지사항을 등록할 수 있습니다.", exception.getMessage());
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

        RuntimeException exception = assertThrows(NoticeNotFoundException.class, () -> {
            noticeService.deleteNotice(noticeNo, role);
        });

        assertEquals("해당 공지사항이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("전체 공지사항 목록 조회 - 고정글 및 생성일 순 정렬 확인")
    void getAllNotices_Success() {
        Long memberNo = 1L;

        Notice notice1 = Notice.builder()
                .noticeTitle("공지사항 1")
                .noticeContent("내용 1")
                .memberNo(memberNo)
                .isFixed(true)
                .build();
        ReflectionTestUtils.setField(notice1, "noticeNo", 1L);

        Notice notice2 = Notice.builder()
                .noticeTitle("공지사항 2")
                .noticeContent("내용 2")
                .memberNo(memberNo)
                .isFixed(false)
                .build();
        ReflectionTestUtils.setField(notice2, "noticeNo", 2L);

        List<Notice> mockList = Arrays.asList(notice1, notice2);

        when(noticeRepository.findAllByOrderByIsFixedDescCreatedAtDesc()).thenReturn(mockList);

        List<NoticeResponse> result = noticeService.getAllNotices();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("관리자", result.get(0).memberName());
        assertEquals("공지사항 1", result.get(0).noticeTitle());
    }

    @Test
    @DisplayName("공지사항이 하나도 없을 때 빈 리스트 반환")
    void getAllNotices_Empty() {
        when(noticeRepository.findAllByOrderByIsFixedDescCreatedAtDesc()).thenReturn(List.of());

        List<NoticeResponse> result = noticeService.getAllNotices();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        log.info("테스트 성공 : 빈 리스트 반환 확인");
        verify(noticeRepository, times(1)).findAllByOrderByIsFixedDescCreatedAtDesc();
    }
}