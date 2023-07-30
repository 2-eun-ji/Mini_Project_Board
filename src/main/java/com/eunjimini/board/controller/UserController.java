package com.eunjimini.board.controller;

import com.eunjimini.board.dto.LoginInfo;
import com.eunjimini.board.dto.User;
import com.eunjimini.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/userRegForm")
    public String userRegForm() {
        return "userRegForm";
    }

    // 회원 정보 등록 name, email, password

    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ) {
        userService.addUser(name, email, password);

        return "redirect:/welcome";
    }

    // 회원가입 성공
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    // 로그인 폼
    @GetMapping("/loginform")
    public String loginform() {
        return "loginform";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession httpSession) { // 세션에 값을 저장하기 위한 가장 쉬운 방법 -> Spring이 자동으로 세션을 처리하는 객체를 넣어줌

        try {
            User user = userService.getUser(email);
            if (user.getPassword().equals(password)) {
                System.out.println("암호가 같습니다.");
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName());

                // 권한 정보 -> loginInfo에 추가하기
                List<String> roles = userService.getRoles(user.getUserId());
                loginInfo.setRoles(roles);

                httpSession.setAttribute("loginInfo", loginInfo); // 첫번째 파라미터 key, 두번째 파라미터 값.
                System.out.println("세션에 로그인 정보 저장!");
            } else {
                throw new RuntimeException("암호가 같지 않습니다.");
            }
        } catch (Exception ex) {
            return "redirect:/loginform?error=true";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.removeAttribute("loginInfo");
        return "redirect:/";
    }
}
