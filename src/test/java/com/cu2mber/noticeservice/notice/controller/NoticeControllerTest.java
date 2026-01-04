package com.cu2mber.noticeservice.notice.controller;

import com.cu2mber.noticeservice.common.exception.custom.NoticeNotFoundException;
import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;
import com.cu2mber.noticeservice.notice.service.NoticeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 공지사항 컨트롤러의 API 동작을 검증하는 테스트 클래스입니다.
 * MockMvc를 사용하여 HTTP 요청/응답 및 JSON 데이터 구조를 독립적으로 테스트합니다.
 */
@WebMvcTest(NoticeController.class)
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoticeService noticeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName(value="공지사항 생성 성공")
    void createNotice_success() throws Exception {

        NoticeRequest request = new NoticeRequest("제목", "내용", true);
        NoticeResponse response = new NoticeResponse(1L, "제목", "내용", true, "관리자", LocalDateTime.now());

        given(noticeService.createNotice(any(NoticeRequest.class), anyString(), anyLong())).willReturn(response);

        mockMvc.perform(post("/api/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Role", "ROLE_ADMIN")
                .header("X-Member-No", 1L)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.noticeTitle").value("제목"))
                .andExpect(jsonPath("$.memberName").value("관리자"));
    }

    @Test
    @DisplayName(value="공지사항 수정 성공")
    void updateNotice() throws Exception {
        NoticeRequest request = new NoticeRequest("수정제목", "수정내용", true);
        NoticeResponse response = new NoticeResponse(1L, "수정제목", "수정내용", true, "관리자", LocalDateTime.now());

        given(noticeService.updateNotice(eq(1L), any(NoticeRequest.class), anyString())).willReturn(response);

        mockMvc.perform(patch("/api/notices/{notice-no}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Role", "ROLE_ADMIN")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noticeTitle").value("수정제목"));

        verify(noticeService).updateNotice(eq(1L), any(NoticeRequest.class), anyString());

    }

    @Test
    @DisplayName(value="공지사항 삭제 성공")
    void deleteNotice() throws Exception{
        Long noticeNo = 1L;
        String role = "ROLE_ADMIN";

        mockMvc.perform(delete("/api/notices/{notice-no}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Role", "ROLE_ADMIN"))
                .andExpect(status().isNoContent());

        verify(noticeService).deleteNotice(eq(noticeNo), eq(role));
    }

    @Test
    @DisplayName(value="공지사항 가져오기 성공")
    void getNotice() throws Exception {
        NoticeResponse response = new NoticeResponse(1L, "제목", "내용", true, "관리자", LocalDateTime.now());

        given(noticeService.getNotice(eq(1L))).willReturn(response);

        mockMvc.perform(get("/api/notices/{notice-no}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noticeTitle").value("제목"))
                .andExpect(jsonPath("$.isFixed").value(true));

    }

    @Test
    @DisplayName("존재하지 않는 공지사항 조회 시 404 에러")
    void getNotice_notFound() throws Exception {
        // given
        given(noticeService.getNotice(999L)).willThrow(new NoticeNotFoundException());

        // when & then
        mockMvc.perform(get("/api/notices/{notice-no}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("모든 공지사항 가져오기 성공 - 키워드 없음")
    void getNotices_WithoutKeyword() throws Exception {
        List<NoticeResponse> content = List.of(
                new NoticeResponse(1L, "제목1", "내용", true, "관리자", LocalDateTime.now()),
                new NoticeResponse(2L, "제목2", "내용", true, "관리자", LocalDateTime.now()));

        Pageable pageable = PageRequest.of(0, 10);
        Page<NoticeResponse> pageResponse = new PageImpl<>(content, pageable, content.size());

        given(noticeService.getAllNotices(0, 10, null)).willReturn(pageResponse);

        mockMvc.perform(get("/api/notices")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].noticeTitle").value("제목1"))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("공지사항 키워드 검색 성공 - 키워드 포함")
    void getNotices_WithKeyword() throws Exception {
        String keyword = "안내";
        List<NoticeResponse> content = List.of(
                new NoticeResponse(1L, "[점검] 서버 점검 안내", "내용", true, "관리자", LocalDateTime.now()));

        Pageable pageable = PageRequest.of(0, 10);
        Page<NoticeResponse> pageResponse = new PageImpl<>(content, pageable, content.size());

        given(noticeService.getAllNotices(0, 10, keyword)).willReturn(pageResponse);

        mockMvc.perform(get("/api/notices")
                        .param("page", "0")
                        .param("size", "10")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1)) // 검색 결과가 1개인 상황
                .andExpect(jsonPath("$.content[0].noticeTitle").value("[점검] 서버 점검 안내"))
                .andExpect(jsonPath("$.totalElements").value(1));

    }
}