package com.cu2mber.noticeservice.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공지사항 작성자 정보 표시를 위한 회원(Member) 미러링 엔티티
 *  <p>상세 회원 정보는 'Member-Service'에서 관리하며, 본 엔티티는 JPA 연관관계 매핑 및
 * 이름 출력을 위한 최소 정보(ID, Name)만 보유합니다.</p>
 *  <p>권한(Role)은 API Gateway 헤더 정보를 활용하므로 별도로 저장하지 않습니다.</p>
 */
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column(name = "member_no")
    private Long memberNo;

    @Column(length = 50, nullable = false)
    private String memberName;

}
