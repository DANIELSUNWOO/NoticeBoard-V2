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
                       Model model) {
        Page<Notice> noticePage = noticeService.getNotices(pageable);
        model.addAttribute("noticePage", noticePage);
        // currentPage는 noticePage.getNumber()로 대체 (0부터 시작)
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

    // ── 상세 ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Notice notice = noticeService.getNotice(id);
        model.addAttribute("notice", notice);
        return "notice/detail";
    }

    // ── 등록 폼 ───────────────────────────────────────────────────────────

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("notice", new Notice());
        return "notice/form";
    }

    // ── 등록 처리 ─────────────────────────────────────────────────────────

    @PostMapping
    public String create(@ModelAttribute Notice notice) {
        noticeService.saveNotice(notice);
        return "redirect:/notices";
    }

    // ── 수정 폼 ───────────────────────────────────────────────────────────

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Notice notice = noticeService.getNotice(id);
        model.addAttribute("notice", notice);
        return "notice/edit-form";
    }

    // ── 수정 처리 ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute Notice form) {
        Notice existing = noticeService.getNotice(id);
        existing.update(form.getTitle(), form.getContent(),
                form.isPinned(), form.isSecret());
        noticeService.updateNotice(existing);
        return "redirect:/notices/" + id;
    }

    // ── 삭제 처리 ─────────────────────────────────────────────────────────

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/notices";
    }
}