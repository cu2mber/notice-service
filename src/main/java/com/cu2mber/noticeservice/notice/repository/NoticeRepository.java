package com.cu2mber.noticeservice.notice.repository;

import com.cu2mber.noticeservice.notice.domain.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 공지사항 엔티티에 대한 데이터 액세스를 담당하는 리포지토리입니다.
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /** 제목 검색 + 페이징 (고정글 우선 정렬 유지) */
    @Query("SELECT n FROM Notice n WHERE n.noticeTitle LIKE %:keyword%")
    Page<Notice> findByNoticeTitleContaining(String keyword, Pageable pageable);

    /**
     * 상단 고정된 공지사항을 먼저 가져오고, 그 안에서 최신순 정렬
     * @return
     */
    @Query("SELECT n FROM Notice n ORDER BY n.isFixed DESC, n.createdAt DESC")
    Page<Notice> findAllNoticesWithPaging(Pageable pageable);
}
