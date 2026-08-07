package com.board.controller;

import com.board.entity.dto.BoardDto;
import com.board.service.BoardService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 안드로이드 앱(compose-board-app)이 쓰는 JSON API.
 * 경로·상태코드·필드 이름은 앱과의 계약이므로 바꾸지 않는다.
 */
@RestController
@RequestMapping("/board")
public class BoardApiController {

    private final BoardService boardService;

    public BoardApiController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public void create(@RequestBody BoardDto boardDto) {
        boardService.create(boardDto);
    }

    @GetMapping
    public List<BoardDto> getAll() {
        return boardService.getAll();
    }

    @GetMapping("/{id}")
    public BoardDto getById(@PathVariable Long id) {
        return boardService.getById(id);
    }

    @PatchMapping
    public void update(@RequestBody BoardDto boardDto) {
        boardService.update(boardDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        boardService.deleteById(id);
    }

}
