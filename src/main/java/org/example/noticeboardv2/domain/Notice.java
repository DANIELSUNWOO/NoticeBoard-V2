package org.example.noticeboardv2.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table("notice")
public class Notice {
    @Id
    private long id;
    private String title;
    private String content;
    private String author;
    private long viewCount;
    @Column("is_pinned")
    private boolean pinned;
    @Column("is_secret")
    private boolean secret;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 새 공지 등록할 때 (id, createdAt, updatedAt 제외)
    public Notice(String title, String content, String author,
                  boolean pinned, boolean secret) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.viewCount = 0;
        this.pinned = pinned;
        this.secret = secret;
    }

    // 수정용 메서드
    public void update(String title, String content,
                       boolean pinned, boolean secret) {
        this.title = title;
        this.content = content;
        this.pinned = pinned;
        this.secret = secret;
    }


}

/*
CREATE TABLE IF NOT EXISTS notice (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    title       VARCHAR(200)    NOT NULL,
    content     TEXT            NOT NULL,
    author      VARCHAR(50)     NOT NULL,
    view_count  INT             NOT NULL DEFAULT 0,
    is_pinned   BOOLEAN         NOT NULL DEFAULT FALSE,
    is_secret   BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  DATETIME        NOT NULL,
    updated_at  DATETIME        NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
 */