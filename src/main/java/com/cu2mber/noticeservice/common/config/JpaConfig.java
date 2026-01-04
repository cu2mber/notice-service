package com.cu2mber.noticeservice.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 관련 설정을 관리하는 설정 클래스
 * * <p>주요 기능:
 * <ul>
 * <li>JPA Auditing 활성화: 엔티티의 생성일, 수정일 자동 관리를 지원합니다.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}