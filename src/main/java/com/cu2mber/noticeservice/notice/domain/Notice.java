package com.cu2mber.noticeservice.notice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 공지사항 정보를 담는 엔티티 클래스
 * <p>
 * - AuditingEntityListener: 생성일(createdAt) 자동 기록 활성화
 * - NoArgsConstructor: 무분별한 객체 생성 방지를 위한 접근 제어(PROTECTED) 적용
 * </p>
 */
@Entity
@Table(name="notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeNo;

    @Column(nullable = false)
    private Long memberNo;

    @Column(length = 255, nullable = false)
    private String noticeTitle;

    @Lob
    @Column(nullable = false)
    private String noticeContent;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isFixed;

    @Builder
    public Notice(Long memberNo, String noticeTitle, String noticeContent, Boolean isFixed) {
        this.memberNo = memberNo;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.isFixed = (isFixed != null) ? isFixed : false;
    }

    public void update(String title, String content, Boolean isFixed) {
        if (title != null) this.noticeTitle = title;
        if (content != null) this.noticeContent = content;
        if (isFixed != null) this.isFixed = isFixed;
    }

}
