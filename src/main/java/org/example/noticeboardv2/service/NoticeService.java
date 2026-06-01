package org.example.noticeboardv2.service;

import lombok.RequiredArgsConstructor;
import org.example.noticeboardv2.domain.Notice;
import org.example.noticeboardv2.repository.NoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 전체 목록
    @Transactional(readOnly = true)
    public Iterable<Notice> getNotices() {
        return noticeRepository.findAll();
    }

    // 페이지 목록
    @Transactional(readOnly = true)
    public Page<Notice> getNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    // 단건 조회 + 조회수 증가
    @Transactional
    public Notice getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("공지사항을 찾을 수 없습니다. id=" + id));
        noticeRepository.incrementViewCount(id);
        notice.setViewCount(notice.getViewCount() + 1);
        return notice;
    }

    // 등록
    @Transactional
    public Notice saveNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    // 수정
    @Transactional
    public Notice updateNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    // 삭제
    @Transactional
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }
}