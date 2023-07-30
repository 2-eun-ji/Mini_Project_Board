package com.eunjimini.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor // 기본 생성자 자동 생성
@ToString // toString() 메소드 자동 생성
public class User {
    private Long userId;
    private String email;
    private String name;
    private String password;
    private LocalDateTime regdate; // advanced ++) 날짜 타입으로 읽어온 다음에 문자열로 변환하는 과정을 거쳐오게 해보기
}

/*
'user_id', 'int', 'NO', 'PRI', NULL, 'auto_increment'
'email', 'varchar(255)', 'NO', '', NULL, ''
'name', 'varchar(50)', 'NO', '', NULL, ''
'password', 'varchar(500)', 'NO', '', NULL, ''
'regdate', 'timestamp', 'YES', '', 'CURRENT_TIMESTAMP', 'DEFAULT_GENERATED'
 */