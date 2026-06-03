package org.example.noticeboardv2.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.noticeboardv2.domain.Member;
import org.example.noticeboardv2.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    // ── 회원가입 폼 ───────────────────────────────────────────────────────

    @GetMapping("/register")
    public String registerForm() {
        return "member/register";
    }

    // ── 회원가입 처리 ─────────────────────────────────────────────────────

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           Model model) {
        try {
            memberService.register(username, password, email);
            return "redirect:/member/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/register";
        }
    }

    // ── 로그인 폼 ─────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    // ── 로그인 처리 ───────────────────────────────────────────────────────

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse response,
                        Model model) {
        Optional<Member> member = memberService.login(username, password);

        if (member.isEmpty()) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 틀렸습니다.");
            return "member/login";
        }

        // 로그인 성공 → Cookie에 username과 role 저장
        Cookie usernameCookie = new Cookie("loginUser", username);
        usernameCookie.setMaxAge(60 * 60 * 24);  // 24시간
        usernameCookie.setPath("/");
        response.addCookie(usernameCookie);

        Cookie roleCookie = new Cookie("loginRole", member.get().getRole());
        roleCookie.setMaxAge(60 * 60 * 24);
        roleCookie.setPath("/");
        response.addCookie(roleCookie);

        return "redirect:/notices";
    }

    // ── 로그아웃 ──────────────────────────────────────────────────────────

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie usernameCookie = new Cookie("loginUser", null);
        usernameCookie.setMaxAge(0);
        usernameCookie.setPath("/");
        response.addCookie(usernameCookie);

        Cookie roleCookie = new Cookie("loginRole", null);
        roleCookie.setMaxAge(0);
        roleCookie.setPath("/");
        response.addCookie(roleCookie);

        return "redirect:/member/login";
    }
}