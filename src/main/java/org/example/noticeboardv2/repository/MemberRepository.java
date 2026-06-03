package org.example.noticeboardv2.repository;

import org.example.noticeboardv2.domain.Member;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    // 아이디로 회원 조회 (로그인 시 사용)
    @Query("SELECT * FROM member WHERE username = :username")
    Optional<Member> findByUsername(@Param("username") String username);

    // 아이디 중복 체크
    @Query("SELECT COUNT(*) FROM member WHERE username = :username")
    int countByUsername(@Param("username") String username);

    // 이메일 중복 체크
    @Query("SELECT COUNT(*) FROM member WHERE email = :email")
    int countByEmail(@Param("email") String email);
}