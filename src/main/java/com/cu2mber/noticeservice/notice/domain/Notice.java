package com.cu2mber.noticeservice.notice.domain;

import com.cu2mber.noticeservice.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name="notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

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
    public Notice(Member member, String noticeTitle, String noticeContent, Boolean isFixed) {
        this.member = member;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.isFixed = (isFixed != null) ? isFixed : false;
    }

}
