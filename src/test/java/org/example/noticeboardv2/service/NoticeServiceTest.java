package org.example.noticeboardv2.service;

import org.example.noticeboardv2.domain.Notice;
import org.example.noticeboardv2.repository.NoticeRepository;
import org.example.noticeboardv2.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional  // 각 테스트 후 MySQL 롤백 → 데이터 안 남음
class NoticeServiceTest {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeRepository noticeRepository;

    @BeforeEach
    void setUp() {
        noticeRepository.deleteAll();
    }

    @Test
    @DisplayName("공지사항 등록이 정상적으로 되어야 한다")
    void 공지사항_등록() {
        // given
        Notice notice = new Notice("테스트 제목", "테스트 내용", "작성자", false, false);

        // when
        Notice saved = noticeService.saveNotice(notice);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("테스트 제목");
        assertThat(saved.getAuthor()).isEqualTo("작성자");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("공지사항 목록 페이징이 정상적으로 되어야 한다")
    void 공지사항_목록_페이징() {
        // given - 15개 등록
        for (int i = 1; i <= 15; i++) {
            noticeService.saveNotice(
                    new Notice("제목" + i, "내용" + i, "작성자", false, false)
            );
        }

        // when
        PageRequest pageable = PageRequest.of(0, 10,
                Sort.by("created_at").descending());
        Page<Notice> page = noticeService.getNotices(pageable);

        // then
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getContent().size()).isEqualTo(10);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("조회 시 조회수가 1 증가해야 한다")
    void 공지사항_조회수_증가() {
        // given
        Notice saved = noticeService.saveNotice(
                new Notice("제목", "내용", "작성자", false, false)
        );

        // when
        Notice found = noticeService.getNotice(saved.getId());

        // then
        assertThat(found.getViewCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("공지사항 수정이 정상적으로 되어야 한다")
    void 공지사항_수정() {
        // given
        Notice saved = noticeService.saveNotice(
                new Notice("원래 제목", "원래 내용", "작성자", false, false)
        );

        // when
        saved.update("수정된 제목", "수정된 내용", true, false);
        noticeService.updateNotice(saved);
        Notice updated = noticeService.getNotice(saved.getId());

        // then
        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
        assertThat(updated.isPinned()).isTrue();
    }

    @Test
    @DisplayName("삭제 후 조회하면 예외가 발생해야 한다")
    void 공지사항_삭제() {
        // given
        Notice saved = noticeService.saveNotice(
                new Notice("제목", "내용", "작성자", false, false)
        );

        // when
        noticeService.deleteNotice(saved.getId());

        // then
        assertThrows(Exception.class,
                () -> noticeService.getNotice(saved.getId()));
    }

    @Test
    @DisplayName("고정글은 목록 상단에 위치해야 한다")
    void 고정글_상단_정렬() {
        // given
        noticeService.saveNotice(new Notice("일반글1", "내용", "작성자", false, false));
        noticeService.saveNotice(new Notice("일반글2", "내용", "작성자", false, false));
        noticeService.saveNotice(new Notice("고정글", "내용", "작성자", true, false));

        // when
        PageRequest pageable = PageRequest.of(0, 10,
                Sort.by("is_pinned").descending()
                        .and(Sort.by("created_at").descending()));
        Page<Notice> page = noticeService.getNotices(pageable);

        // then
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("고정글");
    }
}