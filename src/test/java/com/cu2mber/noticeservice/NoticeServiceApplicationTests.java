package com.cu2mber.noticeservice;

import com.cu2mber.noticeservice.notice.dto.NoticeRequest;
import com.cu2mber.noticeservice.notice.dto.NoticeResponse;
import com.cu2mber.noticeservice.notice.repository.NoticeRepository;
import com.cu2mber.noticeservice.notice.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * 공지사항 서비스의 전체 컨텍스트 로드 및 통합 테스트를 담당하는 클래스
 * 실제 스프링 빈을 모두 로드하여 서비스와 리포지토리 간의 상호작용을 검증합니다.
 */
@SpringBootTest
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
