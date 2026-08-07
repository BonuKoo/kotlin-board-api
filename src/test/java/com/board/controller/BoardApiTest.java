package com.board.controller;

import com.board.entity.BoardEntity;
import com.board.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HTTP 계약을 검증한다.
 *
 * 안드로이드 앱과는 이 계약으로만 연결되므로, 상태 코드와 JSON 필드 이름이
 * 바뀌면 앱이 조용히 깨진다. 그 경계를 실제 요청으로 확인한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BoardApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;

    private BoardEntity 저장된_게시글() {
        return 저장된_게시글("첫 게시글", "내용 A", "홍길동");
    }

    private BoardEntity 저장된_게시글(String title) {
        return 저장된_게시글(title, "내용 A", "홍길동");
    }

    private BoardEntity 저장된_게시글(String title, String content, String name) {
        return boardRepository.save(new BoardEntity(title, content, name));
    }

    private byte[] json(String body) {
        return body.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("게시글을 생성한다")
    void 게시글을_생성한다() throws Exception {
        mockMvc.perform(
                post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{\"title\":\"제목\",\"content\":\"내용\",\"name\":\"작성자\"}"))
        ).andExpect(status().isOk());

        List<BoardEntity> saved = boardRepository.findAll();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("전체 조회는 배열을 반환한다")
    void 전체_조회는_배열을_반환한다() throws Exception {
        저장된_게시글("첫 번째");
        저장된_게시글("두 번째");

        mockMvc.perform(get("/board"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    @DisplayName("단건 조회는 앱이 기대하는 필드를 모두 담는다")
    void 단건_조회는_앱이_기대하는_필드를_모두_담는다() throws Exception {
        BoardEntity saved = 저장된_게시글();

        mockMvc.perform(get("/board/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("첫 게시글"))
                .andExpect(jsonPath("$.content").value("내용 A"))
                .andExpect(jsonPath("$.name").value("홍길동"));
    }

    @Test
    @DisplayName("없는 게시글을 조회하면 404 를 반환한다")
    void 없는_게시글을_조회하면_404_를_반환한다() throws Exception {
        mockMvc.perform(get("/board/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("게시글을 수정한다")
    void 게시글을_수정한다() throws Exception {
        BoardEntity saved = 저장된_게시글();

        mockMvc.perform(
                patch("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{\"id\":" + saved.getId()
                                + ",\"title\":\"고친 제목\",\"content\":\"고친 내용\",\"name\":\"무시됨\"}"))
        ).andExpect(status().isOk());

        BoardEntity updated = boardRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("고친 제목");
        assertThat(updated.getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("없는 게시글을 수정하면 404 를 반환한다")
    void 없는_게시글을_수정하면_404_를_반환한다() throws Exception {
        mockMvc.perform(
                patch("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{\"id\":999,\"title\":\"제목\",\"content\":\"내용\",\"name\":\"작성자\"}"))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("게시글을 삭제한다")
    void 게시글을_삭제한다() throws Exception {
        BoardEntity saved = 저장된_게시글();

        mockMvc.perform(delete("/board/{id}", saved.getId()))
                .andExpect(status().isOk());

        assertThat(boardRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("없는 게시글을 삭제하면 404 를 반환한다")
    void 없는_게시글을_삭제하면_404_를_반환한다() throws Exception {
        mockMvc.perform(delete("/board/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("한글이 그대로 오간다")
    void 한글이_그대로_오간다() throws Exception {
        mockMvc.perform(
                post("/board")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{\"title\":\"한글 제목\",\"content\":\"한글 내용\",\"name\":\"홍길동\"}"))
        ).andExpect(status().isOk());

        BoardEntity saved = boardRepository.findAll().get(0);

        mockMvc.perform(get("/board/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("한글 제목"))
                .andExpect(jsonPath("$.name").value("홍길동"));
    }
}
