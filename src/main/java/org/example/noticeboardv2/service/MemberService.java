package org.example.noticeboardv2.service;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv2.domain.Member;
import org.example.noticeboardv2.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원가입
    @Transactional
    public void register(String username, String password, String email) {
        if (memberRepository.countByUsername(username) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.countByEmail(email) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        Member member = new Member(username, password, email, "ROLE_USER");
        memberRepository.save(member);
    }

    // 로그인 - username/password 확인 후 Member 반환
    @Transactional(readOnly = true)
    public Optional<Member> login(String username, String password) {
        return memberRepository.findByUsername(username)
                .filter(m -> m.getPassword().equals(password));
    }

    // username으로 회원 조회
    @Transactional(readOnly = true)
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }
}