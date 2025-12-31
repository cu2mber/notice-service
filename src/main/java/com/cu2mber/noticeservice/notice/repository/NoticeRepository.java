package com.cu2mber.noticeservice.notice.repository;

import com.cu2mber.noticeservice.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /**
     * 상단 고정된 공지사항을 먼저 가져오고, 그 안에서 최신순 정렬
     * @return
     */
    List<Notice> findAllByOrderByIsFixedDescCreatedAtDesc();
}
