package org.example.noticeboardv2.controller;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv2.domain.Notice;
import org.example.noticeboardv2.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
        if (!isLoggedIn(request)) return "redirect:/member/login";

        Page<Notice> noticePage = noticeService.getNotices(pageable);
        model.addAttribute("noticePage", noticePage);
        model.addAttribute("isAdmin", isAdmin(request));
        model.addAttribute("loginUser", getCookieValue(request, "loginUser"));
        return "notice/list";
    }

//
//    @GetMapping("")
//    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
//        PageRequest pageable = PageRequest.of(
//                page - 1,
//                10,
//                Sort.by("is_pinned").descending()
//                        .and(Sort.by("created_at").descending())
//        );
//        Page<Notice> noticePage = noticeService.getNotices(pageable);
//        model.addAttribute("noticePage", noticePage);
//        model.addAttribute("currentPage", page);
//        return "notice/list";
//    }

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
        if (!isLoggedIn(request)) return "redirect:/member/login";

        Notice notice = noticeService.getNotice(id);

        // 비밀글은 관리자만 접근 가능
        if (notice.isSecret() && !isAdmin(request)) {
            return "redirect:/notices";
        }

        model.addAttribute("notice", notice);
        model.addAttribute("isAdmin", isAdmin(request));
        model.addAttribute("loginUser", getCookieValue(request, "loginUser"));
        return "notice/detail";
    }
    // ── 등록 폼 ───────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String createForm(HttpServletRequest request, Model model) {
        if (!isAdmin(request)) return "redirect:/notices";
        model.addAttribute("notice", new Notice());
        return "notice/form";
    }
    // ── 등록 처리 ─────────────────────────────────────────────────────────

    @PostMapping
    public String create(@ModelAttribute Notice notice,
                         HttpServletRequest request) {
        if (!isAdmin(request)) return "redirect:/notices";
        noticeService.saveNotice(notice);
        return "redirect:/notices";
    }

    // ── 수정 폼 ───────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           HttpServletRequest request, Model model) {
        if (!isAdmin(request)) return "redirect:/notices";
        Notice notice = noticeService.getNotice(id);
        model.addAttribute("notice", notice);
        return "notice/edit-form";
    }

    // ── 수정 처리 ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @ModelAttribute Notice form,
                       HttpServletRequest request) {
        if (!isAdmin(request)) return "redirect:/notices";
        Notice existing = noticeService.getNotice(id);
        existing.update(form.getTitle(), form.getContent(),
                form.isPinned(), form.isSecret());
        noticeService.updateNotice(existing);
        return "redirect:/notices/" + id;
    }

    // ── 삭제 처리 ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         HttpServletRequest request) {
        if (!isAdmin(request)) return "redirect:/notices";
        noticeService.deleteNotice(id);
        return "redirect:/notices";
    }
}