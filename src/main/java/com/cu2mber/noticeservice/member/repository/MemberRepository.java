package com.cu2mber.noticeservice.member.repository;

import com.cu2mber.noticeservice.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
