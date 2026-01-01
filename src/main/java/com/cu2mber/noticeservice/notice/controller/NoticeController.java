package com.cu2mber.noticeservice.notice.controller;

import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;
import com.cu2mber.noticeservice.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(@Valid @RequestBody NoticeRequest request,
                                                       @RequestHeader("X-Role") String role,
                                                       @RequestHeader("X-Member-No") Long memberNo) {
        NoticeResponse notice = noticeService.createNotice(request, role, memberNo);

        return ResponseEntity.status(HttpStatus.CREATED).body(notice);
    }




}