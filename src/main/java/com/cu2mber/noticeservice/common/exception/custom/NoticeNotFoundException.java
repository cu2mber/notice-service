package com.cu2mber.noticeservice.common.exception.custom;

public class NoticeNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "존재하지 않는 공지사항입니다.";

    public NoticeNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}