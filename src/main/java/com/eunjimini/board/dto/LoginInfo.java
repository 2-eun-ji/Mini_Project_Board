package com.eunjimini.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
// @AllArgsConstructor -> 모든 필드에 대한 생성자가 만들어지고 초기화
public class LoginInfo {
    private Long userId;
    private String email;
    private String name;
    private List<String> roles = new ArrayList<>();

    public LoginInfo(Long userId, String email, String name) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }

    // 사용되진 않지만, 만들었으니까 일단 그냥 두는걸로
    public void addRole(String roleName) {
        roles.add(roleName);
    }
}
