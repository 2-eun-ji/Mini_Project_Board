package com.eunjimini.board.controller;

import com.eunjimini.board.dto.Board;
import com.eunjimini.board.dto.LoginInfo;
import com.eunjimini.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

// HTTP 요청을 받아서 응답하는 컴포넌트. 자동 Bean 생성
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시물 목록
    @GetMapping("/")
    public String list(@RequestParam(name="page", defaultValue = "1") int page, HttpSession httpSession, Model model) { // HttpSession, Model은 Spring이 자동으로 넣음
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        model.addAttribute("loginInfo", loginInfo); // 템플릿에게 넘김

        // 페이징 처리
        int totalCount = boardService.getTotalCount();
        List<Board> list = boardService.getBoards(page); // 페이지가 1부터...
        int pageCount = totalCount / 10; // 1만 보기
        if(totalCount % 10 > 0) { // 나머지 더 있으면 1페이지 추가
            pageCount++;
        }
        int currentPage = page;

        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);
        return "list";
    }

    // /board?id=3 // 물음표 이하를 파라미터 id, 파라미터 id의 값은 3이라는 뜻
    @GetMapping("/board")
    public String board(@RequestParam("boardId") Long boardId, Model model){
        System.out.println("boardId : " + boardId);

        Board board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "board";
    }

    @GetMapping("/writeForm")
    public String writeForm(HttpSession httpSession, Model model) {
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");
        if(loginInfo == null) { // 세션에 로그인 정보 X 일 경우 /loginForm으로 리다이렉트
            return "redirect:/";
        }

        model.addAttribute("loginInfo", loginInfo);

        return "writeForm";
    }

    // 로그인한 사용자만 글 O , 로그인 X면 list 보기로 자동 이동
    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession httpSession) {
        LoginInfo loginInfo = (LoginInfo)httpSession.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }
        System.out.println("title : " + title);

        boardService.addBoard(loginInfo.getUserId(), title, content);

        // 로그인 한 회원 정보, 제목, 내용 저장
        return "redirect:/"; // 홈으로 리다이렉트
    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") Long boardId,
            HttpSession httpSession
    ) {
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null) {
            return "redirect:/loginform";
        }

        // loginInfo.getUserId() 사용자가 쓴 글일 경우 삭제가능
        List<String> roles = loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            boardService.deleteBoard(boardId);
        }else {
            boardService.deleteBoard(loginInfo.getUserId(), boardId);
        }

        return "redirect:/"; // 글 목록으로 리다이렉트
    }

    @GetMapping("/updateform")
    public String updateform(@RequestParam("boardId") Long boardId, Model model,  HttpSession session){
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }
        // boardId에 해당하는 정보를 읽어와 updateform 템플릿에 전달
        Board board = boardService.getBoard(boardId, false);
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform";
    }

    // boardId에 해당하는 글 제목, 내용 수정 (글쓴이만 가능)
    @PostMapping("/update")
    public String update(@RequestParam("boardId") Long boardId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession session
    ){

        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }

        // 글쓴이만 수정 가능
        Board board = boardService.getBoard(boardId, false);
        if(board.getUserId() != loginInfo.getUserId()){
            return "redirect:/board?boardId=" + boardId; // 글보기로 이동
        }
        // boardId에 해당하는 글 제목/내용을 수정
        boardService.updateBoard(boardId, title, content);
        return "redirect:/board?boardId=" + boardId; // 수정된 글 보기로 리다이렉트
    }
}