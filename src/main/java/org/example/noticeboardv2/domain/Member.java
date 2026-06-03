package org.example.noticeboardv2.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table("member")
public class Member {

    @Id
    private Long id;

    private String username;
    private String password;
    private String email;
    private String role;  // ROLE_ADMIN, ROLE_USER

    // 회원가입용 생성자 (id 제외)
    public Member(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(this.role);
    }
}