package com.eunjimini.board.service;

import com.eunjimini.board.Repository.UserRepository;
import com.eunjimini.board.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 트랜잭션 단위로 실행될 메소드를 선언하고 있는 클래스
@Service
@RequiredArgsConstructor // lombok에서 final 필드를 초기화하는 생성자를 자동 생성해 줌
public class UserService {
    // 회원 정보 저장
    private final UserRepository userRepository; // 어노테이션을 사용했기 때문에 final만 선언되어 있고 생성자를 따로 만들지 않아도 됨

    @Transactional // 서비스가 가지고 있는 메서드에는 이 어노테이션이 붙고, 하나의 트랜잭션으로 처리됨
    public User addUser(String name, String email, String password) {
        User user1 = userRepository.getUser(email); // 이메일 중복 검사하기
        if (user1 != null) {
            throw new RuntimeException("이미 가입한 이메일입니다.");
        }

        User user = userRepository.addUser(email, name, password); // 회원정보 값 저장
        userRepository.mappingUserRole(user.getUserId()); // 권한 부여 매핑

        return user;
    }

    // 회원정보를 가져오는 로직
    @Transactional
    public User getUser(String email) {
        return userRepository.getUser(email);
    }

    @Transactional
    public List<String> getRoles(Long userId) {
        return userRepository.getRoles(userId);
    }
}
