package com.board.controller;

import com.board.entity.BoardEntity;
import com.board.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * JSP 화면 경로를 검증한다.
 *
 * MockMvc 에는 서블릿 컨테이너가 없어 .jsp 가 실제로 렌더링되지는 않는다.
 * 대신 어떤 뷰로 forward 되는지, 모델에 무엇이 담기는지, 쓰기 요청 뒤
 * 어디로 redirect 되는지를 확인한다. 화면이 그려지는지는 별개로 앱을 띄워 본다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BoardViewTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;

    private BoardEntity 저장된_게시글() {
        return boardRepository.save(new BoardEntity("첫 게시글", "내용 A", "홍길동"));
    }

    @Test
    @DisplayName("루트로 들어오면 목록으로 보낸다")
    void 루트로_들어오면_목록으로_보낸다() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards"));
    }

    @Test
    @DisplayName("목록 화면은 게시글을 모델에 담아 list.jsp 로 넘긴다")
    void 목록_화면은_게시글을_모델에_담아_list_jsp_로_넘긴다() throws Exception {
        저장된_게시글();

        mockMvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list"))
                .andExpect(model().attributeExists("boards"))
                .andExpect(forwardedUrl("/WEB-INF/views/board/list.jsp"));
    }

    @Test
    @DisplayName("작성 폼은 빈 게시글과 함께 form.jsp 로 넘긴다")
    void 작성_폼은_빈_게시글과_함께_form_jsp_로_넘긴다() throws Exception {
        mockMvc.perform(get("/boards/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/form"))
                .andExpect(model().attribute("editing", false))
                .andExpect(forwardedUrl("/WEB-INF/views/board/form.jsp"));
    }

    @Test
    @DisplayName("폼으로 등록하면 저장하고 목록으로 되돌린다")
    void 폼으로_등록하면_저장하고_목록으로_되돌린다() throws Exception {
        mockMvc.perform(
                post("/boards")
                        .param("title", "폼에서 쓴 글")
                        .param("content", "폼 내용")
                        .param("name", "홍길동")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards"));

        assertThat(boardRepository.findAll())
                .extracting(BoardEntity::getTitle)
                .containsExactly("폼에서 쓴 글");
    }

    @Test
    @DisplayName("상세 화면은 detail.jsp 로 넘긴다")
    void 상세_화면은_detail_jsp_로_넘긴다() throws Exception {
        BoardEntity saved = 저장된_게시글();

        mockMvc.perform(get("/boards/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("board/detail"))
                .andExpect(model().attributeExists("board"))
                .andExpect(forwardedUrl("/WEB-INF/views/board/detail.jsp"));
    }

    @Test
    @DisplayName("수정 폼은 기존 값과 함께 form.jsp 로 넘긴다")
    void 수정_폼은_기존_값과_함께_form_jsp_로_넘긴다() throws Exception {
        BoardEntity saved = 저장된_게시글();

        mockMvc.perform(get("/boards/{id}/edit", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("board/form"))
                .andExpect(model().attribute("editing", true))
                .andExpect(model().attributeExists("board"));
    }

    @Test
    @DisplayName("폼으로 수정하면 제목만 바뀌고 작성자는 유지된다")
    void 폼으로_수정하면_제목만_바뀌고_작성자는_유지된다() throws Exception {
        BoardEntity saved = 저장된_게시글();

        mockMvc.perform(
                post("/boards/{id}/edit", saved.getId())
                        .param("title", "고친 제목")
                        .param("content", "고친 내용")
                        .param("name", "다른 사람")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + saved.getId()));

        BoardEntity updated = boardRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("고친 제목");
        assertThat(updated.getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("폼으로 삭제하면 지우고 목록으로 되돌린다")
    void 폼으로_삭제하면_지우고_목록으로_되돌린다() throws Exception {
        BoardEntity saved = 저장된_게시글();

        mockMvc.perform(post("/boards/{id}/delete", saved.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards"));

        assertThat(boardRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("없는 게시글을 열면 JSON 이 아니라 404 에러 화면을 준다")
    void 없는_게시글을_열면_JSON_이_아니라_404_에러_화면을_준다() throws Exception {
        mockMvc.perform(get("/boards/999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/notFound"))
                .andExpect(forwardedUrl("/WEB-INF/views/error/notFound.jsp"));
    }
}
