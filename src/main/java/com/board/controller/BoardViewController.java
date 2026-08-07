package com.board.controller;

import com.board.entity.dto.BoardDto;
import com.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * JSP 화면을 돌려주는 컨트롤러.
 *
 * 앱이 쓰는 {@link BoardApiController} 와 서비스 계층을 공유하되,
 * 이쪽은 JSON 대신 뷰 이름을 반환한다. 실제 렌더링은
 * src/main/webapp/WEB-INF/views 아래 .jsp 가 맡는다.
 *
 * HTML form 은 GET 과 POST 만 보낼 수 있어 수정·삭제도 POST 로 받는다.
 * 쓰기 요청 뒤에는 redirect 로 응답해 새로고침 시 중복 전송을 막는다(PRG).
 */
@Controller
@RequestMapping("/boards")
public class BoardViewController {

    private final BoardService boardService;

    public BoardViewController(BoardService boardService) {
        this.boardService = boardService;
    }

    /** 목록 */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("boards", boardService.getAll());
        return "board/list";
    }

    /** 작성 폼 */
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("board", new BoardDto());
        model.addAttribute("editing", false);
        return "board/form";
    }

    /** 작성 처리 */
    @PostMapping
    public String create(@ModelAttribute BoardDto board) {
        boardService.create(board);
        return "redirect:/boards";
    }

    /** 상세 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.getById(id));
        return "board/detail";
    }

    /** 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.getById(id));
        model.addAttribute("editing", true);
        return "board/form";
    }

    /** 수정 처리 */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute BoardDto board) {
        // 폼이 보낸 id 를 믿지 않고 경로의 id 를 쓴다.
        board.setId(id);
        boardService.update(board);
        return "redirect:/boards/" + id;
    }

    /** 삭제 처리 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        boardService.deleteById(id);
        return "redirect:/boards";
    }
}
