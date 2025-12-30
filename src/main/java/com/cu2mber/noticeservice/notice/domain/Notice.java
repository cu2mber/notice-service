package com.cu2mber.noticeservice.notice.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="notices")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long NoticeNo;

    Long memberNo;

    @Column(length = 255, nullable = false)
    String noticeTitle;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    Boolean isFixed;


}
