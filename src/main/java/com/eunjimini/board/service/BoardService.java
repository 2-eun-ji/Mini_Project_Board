package com.eunjimini.board.service;

import com.eunjimini.board.Repository.BoardRepository;
import com.eunjimini.board.dto.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public void addBoard(Long userId, String title, String content) {
        boardRepository.addBoard(userId, title, content);
    }


    @Transactional(readOnly = true) // 조회만 하는 메소드이기 때문에 성능을 조금이라도 높이기 위해 읽기 전용으로 만들기 (select할 때)
    public int getTotalCount() {
        return boardRepository.getTotalCount();
    }

    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        return boardRepository.getBoards(page);
    }

    @Transactional
    public Board getBoard(Long boardId) {
        // id에 해당하는 게시물을 읽어오고, 조회수도 1 증가시키기
        return getBoard(boardId, true);
    }

    // updateViewCnt = true (글 조회수 증가) / updateViewCnt = false (증가 X)
    @Transactional
    public Board getBoard(Long boardId, boolean updateViewCnt){
        Board board = boardRepository.getBoard(boardId);
        if(updateViewCnt) {
            boardRepository.updateViewCnt(boardId);
        }
        return board;
    }

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.getBoard(boardId);
        if (board.getUserId() == userId) {
            boardRepository.deleteBoard(boardId);
        }
    }

    // 메소드 오버로딩
    @Transactional
    public void deleteBoard(Long boardId) {
        boardRepository.deleteBoard(boardId);
    }

    @Transactional
    public void updateBoard(Long boardId, String title, String content) {
        boardRepository.updateBoard(boardId, title, content);
    }
}
