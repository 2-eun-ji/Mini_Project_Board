package com.eunjimini.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Board {
    private Long boardId;
    private String title;
    private String content;
    private String name; // join을 위한 컬럼 추가
    private Long userId;
    private LocalDateTime regdate;
    private int viewCnt;
}

/*
'board_id', 'int', 'NO', 'PRI', NULL, 'auto_increment'
'title', 'varchar(100)', 'NO', '', NULL, ''
'content', 'text', 'YES', '', NULL, ''
'user_id', 'int', 'NO', 'MUL', NULL, ''
'regdate', 'timestamp', 'YES', '', 'CURRENT_TIMESTAMP', 'DEFAULT_GENERATED'
'view_cnt', 'int', 'YES', '', '0', ''
 */