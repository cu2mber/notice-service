package com.cu2mber.noticeservice.notice.controller;

import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;
import com.cu2mber.noticeservice.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 공지사항 관리를 위한 REST API 컨트롤러입니다.
 * CRUD 작업 및 페이징 기반의 검색 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /** 공지사항 등록 */
    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(@Valid @RequestBody NoticeRequest request,
                                                       @RequestHeader("X-Role") String role,
                                                       @RequestHeader("X-Member-No") Long memberNo) {
        NoticeResponse notice = noticeService.createNotice(request, role, memberNo);
        return ResponseEntity.status(HttpStatus.CREATED).body(notice);
    }

    /** 공지사항 수정 */
    @PatchMapping("/{notice-no}")
    public ResponseEntity<NoticeResponse> updateNotice(@PathVariable("notice-no") Long noticeNo,
                                                       @Valid @RequestBody NoticeRequest request,
                                                       @RequestHeader("X-Role") String role){
        NoticeResponse response = noticeService.updateNotice(noticeNo, request, role);
        return ResponseEntity.ok(response);
    }

    /** 공지사항 삭제 */
    @DeleteMapping("/{notice-no}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("notice-no") Long noticeNo,
                                             @RequestHeader("X-Role") String role){
        noticeService.deleteNotice(noticeNo, role);
        return ResponseEntity.noContent().build();
    }

    /** 공지사항 상세 조회 */
    @GetMapping("/{notice-no}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable("notice-no") Long noticeNo) {
        NoticeResponse response = noticeService.getNotice(noticeNo);
        return ResponseEntity.ok(response);
    }

    /** 공지사항 목록 조회 (페이징 및 키워드 검색) */
    @GetMapping
    public ResponseEntity<Page<NoticeResponse>> getAllNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Page<NoticeResponse> responses = noticeService.getAllNotices(page, size, keyword);
        return ResponseEntity.ok(responses);
    }
}