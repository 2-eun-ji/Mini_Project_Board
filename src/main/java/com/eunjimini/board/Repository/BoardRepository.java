package com.eunjimini.board.Repository;

import com.eunjimini.board.dto.Board;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class BoardRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate; // JDBC를 사용하기 위해 선언
    private SimpleJdbcInsertOperations insertBoard; // INSERT를 적지 않아도 insert 가능하게 해주는 인터페이스

    /* JDBC 템플릿 초기화를 위해 dataSource가 필요
    = application.yml 파일에 DB 접속 관련 설정을 해주면 Hikari라는 데이터소스 구현 객체 생성
    -> 스프링 빈이 자동으로 넣어줌 (생성자 주입) = 데이터소스 HikariCP Bean */
    public BoardRepository(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertBoard = new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("board_id"); // 자동 증가 id 설정
    }

    @Transactional
    public void addBoard(Long userId, String title, String content) {
        Board board = new Board();
        board.setUserId(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(board); // DTO 객체만 넣어주면 자동으로 컬럼에 맞게끔 변환
        insertBoard.execute(params);
    }

    @Transactional(readOnly = true)
    public int getTotalCount() {
        String sql = "select count(*) as total_count from board"; // 집합 쿼리는 무조건 1건의 데이터가 나옴
        Integer totalCount = jdbcTemplate.queryForObject(sql, Map.of(), Integer.class); // 비어있는 맵이 하나 리턴
        return totalCount.intValue(); // 정수값 리턴
    }

    // 페이지네이션
    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        // 시작값은 0 이고 10, 20, 30 -> 1page, 2page, 3page...
        int start = (page - 1) * 10;
        String sql = "select b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name from board b, user u where b.user_id = u.user_id  order by board_id desc limit :start, 10";
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        List<Board> list = jdbcTemplate.query(sql, Map.of("start", start), rowMapper);
        return list;
    }

    @Transactional(readOnly = true)
    public Board getBoard(Long boardId) {
        // 1건이나 0건
        String sql = "select b.user_id, b.board_id, b.title, b.regdate, b.view_cnt, u.name, b.content from board b, user u where b.user_id = u.user_id  and b.board_id = :boardId";
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        Board board = jdbcTemplate.queryForObject(sql, Map.of("boardId", boardId), rowMapper);
        return board;
    }

    @Transactional
    public void updateViewCnt(Long boardId) {
        String sql = "update board\n" +
                "set view_cnt = view_cnt + 1\n" +
                "where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        String sql = "delete from board where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void updateBoard(Long boardId, String title, String content) {
        String sql = "update board\n" +
                "set title = :title , content = :content\n" +
                "where board_id = :boardId";
        Board board = new Board();
        board.setBoardId(boardId);
        board.setTitle(title);
        board.setContent(content);
        SqlParameterSource params =  new BeanPropertySqlParameterSource(board);
        jdbcTemplate.update(sql, params);
    }
}