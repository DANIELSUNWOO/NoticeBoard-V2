package org.example.noticeboardv2.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.noticeboardv2.domain.Notice;
import org.example.noticeboardv2.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    // ── 목록 ──────────────────────────────────────────────────────────────

    @GetMapping("")
    public String list(@PageableDefault(size = 10,
                               sort = {"is_pinned", "created_at"},
                               direction = Sort.Direction.DESC) Pageable pageable,
                       HttpServletRequest request, Model model) {

        Page<Notice> noticePage = noticeService.getNotices(pageable);
        model.addAttribute("noticePage", noticePage);
        model.addAttribute("loginUser", getCookieValue(request, "loginUser"));

        if (!isLoggedIn(request)) return "notice/guest/list";   // 비회원
        if (isAdmin(request)) return "notice/admin/list";       // 관리자
        return "notice/user/list";                              // 일반 회원
    }

    // ── 쿠키 헬퍼 메서드 ──────────────────────────────────────────────────

    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) return cookie.getValue();
        }
        return null;
    }

    private boolean isAdmin(HttpServletRequest request) {
        return "ROLE_ADMIN".equals(getCookieValue(request, "loginRole"));
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        return getCookieValue(request, "loginUser") != null;
    }

    // ── 상세 ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         HttpServletRequest request, Model model) {

        Notice notice = noticeService.getNotice(id);
        String loginUser = getCookieValue(request, "loginUser");

        // 비밀글 접근 제어
        if (notice.isSecret()) {
            // 비회원 → 목록으로
            if (!isLoggedIn(request)) return "redirect:/notices";
            // 일반 회원 → 본인 글만
            if (!isAdmin(request) && !notice.getAuthor().equals(loginUser)) {
                return "redirect:/notices";
            }
        }

        model.addAttribute("notice", notice);
        model.addAttribute("loginUser", loginUser);

        if (!isLoggedIn(request)) return "notice/guest/detail";  // 비회원
        if (isAdmin(request)) return "notice/admin/detail";      // 관리자
        return "notice/user/detail";                             // 일반 회원
    }

    // ── 등록 폼 ───────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String createForm(HttpServletRequest request, Model model) {
        if (!isLoggedIn(request)) return "redirect:/member/login";
        model.addAttribute("notice", new Notice());
        return isAdmin(request) ? "notice/admin/form" : "notice/user/form";
    }

    // ── 등록 처리 ─────────────────────────────────────────────────────────

    @PostMapping
    public String create(@ModelAttribute Notice notice,
                         HttpServletRequest request) {
        if (!isLoggedIn(request)) return "redirect:/member/login";

        notice.setAuthor(getCookieValue(request, "loginUser"));
        if (!isAdmin(request)) {
            notice.setPinned(false);
        }

        noticeService.saveNotice(notice);
        return "redirect:/notices";
    }
    // ── 수정 폼 ───────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           HttpServletRequest request, Model model) {
        if (!isLoggedIn(request)) return "redirect:/member/login";

        Notice notice = noticeService.getNotice(id);
        String loginUser = getCookieValue(request, "loginUser");

        if (!notice.getAuthor().equals(loginUser)) {
            return "redirect:/notices/" + id;
        }

        model.addAttribute("notice", notice);
        return isAdmin(request) ? "notice/admin/form" : "notice/user/form";
    }


    // ── 수정 처리 ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @ModelAttribute Notice form,
                       HttpServletRequest request) {
        if (!isLoggedIn(request)) return "redirect:/member/login";

        Notice existing = noticeService.getNotice(id);
        String loginUser = getCookieValue(request, "loginUser");

        if (!existing.getAuthor().equals(loginUser)) {
            return "redirect:/notices/" + id;
        }

        if (isAdmin(request)) {
            existing.update(form.getTitle(), form.getContent(),
                    form.isPinned(), form.isSecret());
        } else {
            existing.update(form.getTitle(), form.getContent(),
                    existing.isPinned(), form.isSecret());
        }

        noticeService.updateNotice(existing);
        return "redirect:/notices/" + id;
    }
    // ── 삭제 처리 ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         HttpServletRequest request) {
        if (!isLoggedIn(request)) return "redirect:/member/login";

        Notice notice = noticeService.getNotice(id);
        String loginUser = getCookieValue(request, "loginUser");

        if (!isAdmin(request) && !notice.getAuthor().equals(loginUser)) {
            return "redirect:/notices/" + id;
        }

        noticeService.deleteNotice(id);
        return "redirect:/notices";
    }
}