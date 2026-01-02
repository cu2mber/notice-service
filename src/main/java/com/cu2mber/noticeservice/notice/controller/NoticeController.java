package com.cu2mber.noticeservice.notice.controller;

import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;
import com.cu2mber.noticeservice.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @DeleteMapping("/{notice-no}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("notice-no") Long noticeNo,
                                             @RequestHeader("X-Role") String role){
        noticeService.deleteNotice(noticeNo, role);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{notice-no}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable("notice-no") Long noticeNo) {
        NoticeResponse response = noticeService.getNotice(noticeNo);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAllNotices(){
        List<NoticeResponse> noticeResponseList = noticeService.getAllNotices();

        return ResponseEntity.ok(noticeResponseList);
    }


}