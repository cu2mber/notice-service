package com.cu2mber.noticeservice;

import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;
import com.cu2mber.noticeservice.notice.repository.NoticeRepository;
import com.cu2mber.noticeservice.notice.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * 공지사항 서비스 통합 테스트
 * <p>
 * {@link SpringBootTest}를 통해 전체 컨텍스트를 로드하며,
 * {@link Transactional} 어노테이션을 통해 테스트 완료 후 데이터를 자동으로 롤백하여
 * 데이터베이스의 독립성을 보장합니다.
 * </p>
 */
@SpringBootTest
@Transactional
class NoticeServiceApplicationTests {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("전체 시스템 통합 테스트: 공지사항 생성 후 조회")
    void noticeFullCycleTest() {
        NoticeRequest request = new NoticeRequest("통합 테스트 제목", "내용", false);
        String role = "ROLE_ADMIN";
        Long memberNo = 1L;

        NoticeResponse savedNotice = noticeService.createNotice(request, role, memberNo);

        NoticeResponse foundNotice = noticeService.getNotice(savedNotice.noticeNo());

        assertThat(foundNotice.noticeTitle()).isEqualTo("통합 테스트 제목");
        assertThat(noticeRepository.existsById(savedNotice.noticeNo())).isTrue();
    }

}
