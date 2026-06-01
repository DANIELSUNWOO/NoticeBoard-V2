package org.example.noticeboardv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@SpringBootApplication
@EnableJdbcAuditing
public class NoticeboardV2Application {

    public static void main(String[] args) {
        SpringApplication.run(NoticeboardV2Application.class, args);
    }

}
