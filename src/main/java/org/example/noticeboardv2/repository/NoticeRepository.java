package org.example.noticeboardv2.repository;

import org.example.noticeboardv2.domain.Notice;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository
        extends CrudRepository<Notice, Long>
        , PagingAndSortingRepository<Notice, Long> {

    // 조회수 1 증가
    @Modifying
    @Query("UPDATE notice SET view_count = view_count + 1 WHERE id = :id")
    void incrementViewCount(@Param("id") Long id);
}
