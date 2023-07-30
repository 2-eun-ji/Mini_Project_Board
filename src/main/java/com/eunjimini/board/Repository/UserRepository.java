package com.eunjimini.board.Repository;

import com.eunjimini.board.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
public class UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate; // JDBC를 사용하기 위해 선언
    private SimpleJdbcInsertOperations insertUser; // INSERT를 적지 않아도 insert 가능하게 해주는 인터페이스

    /* JDBC 템플릿 초기화를 위해 dataSource가 필요
    = application.yml 파일에 DB 접속 관련 설정을 해주면 Hikari라는 데이터소스 구현 객체 생성
    -> 스프링 빈이 자동으로 넣어줌 */
    public UserRepository(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("user_id"); // 자동 증가 id
    }

    // Spring JDBC 사용 코드
    @Transactional
    public User addUser(String email, String name, String password) {
        // insert into user (email, name, password, localDateTime) values (:email, :name, :password, :localdatetime); # user_id auto gen
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(user);
        Number number = insertUser.executeAndReturnKey(params); // 인서트 쿼리 실행, 자동으로 생성된 id값 가져옴
        int userId = number.intValue();
        user.setUserId(Long.valueOf(userId));
        return user;
    }

    // SimpleJDBCInsert 안 쓰고, 직접 SQL 사용
    @Transactional
    public void mappingUserRole(Long userId) {
        // insert into user_role( user_id, role_id ) values ( ?, 1);
        String sql = "insert into user_role( user_id, role_id ) values (:userId, 1)";
        SqlParameterSource params = new MapSqlParameterSource("userId", userId); // 보통 Map 객체 사용
        jdbcTemplate.update(sql, params);
    }

    @Transactional
    public User getUser(String email) {
        try {
            // user_id => setUserId , email => setEmail ...
            String sql = "select user_id, email, name, password, regdate from user where email = :email";
            SqlParameterSource params = new MapSqlParameterSource("email", email);
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            User user = jdbcTemplate.queryForObject(sql, params, rowMapper);
            return user;
        } catch (Exception ex) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(Long userId) {
        String sql = "select r.name from user_role ur, role r where ur.role_id = r.role_id and ur.user_id = :userId";

        List<String> roles = jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> {
            return rs.getString(1);
        });
        return roles;
    }
}
