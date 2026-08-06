package com.board.controller

import com.board.entity.BoardEntity
import com.board.repository.BoardRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

/**
 * HTTP 계약을 검증한다.
 *
 * 안드로이드 앱과는 이 계약으로만 연결되므로, 상태 코드와 JSON 필드 이름이
 * 바뀌면 앱이 조용히 깨진다. 그 경계를 실제 요청으로 확인한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BoardApiTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val boardRepository: BoardRepository,
) {

    private fun 저장된_게시글(
        title: String = "첫 게시글",
        content: String = "내용 A",
        name: String = "홍길동",
    ): BoardEntity = boardRepository.save(
        BoardEntity(title = title, content = content, name = name)
    )

    private fun json(body: String) = body.toByteArray(Charsets.UTF_8)

    @Test
    fun `게시글을 생성한다`() {
        mockMvc.perform(
            post("/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("""{"title":"제목","content":"내용","name":"작성자"}"""))
        ).andExpect(status().isOk)

        val saved = boardRepository.findAll()
        assert(saved.size == 1)
        assert(saved[0].title == "제목")
    }

    @Test
    fun `전체 조회는 배열을 반환한다`() {
        저장된_게시글(title = "첫 번째")
        저장된_게시글(title = "두 번째")

        mockMvc.perform(get("/board"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").exists())
            .andExpect(jsonPath("$[0].name").exists())
    }

    @Test
    fun `단건 조회는 앱이 기대하는 필드를 모두 담는다`() {
        val saved = 저장된_게시글()

        mockMvc.perform(get("/board/{id}", saved.id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(saved.id))
            .andExpect(jsonPath("$.title").value("첫 게시글"))
            .andExpect(jsonPath("$.content").value("내용 A"))
            .andExpect(jsonPath("$.name").value("홍길동"))
    }

    @Test
    fun `없는 게시글을 조회하면 404 를 반환한다`() {
        mockMvc.perform(get("/board/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `게시글을 수정한다`() {
        val saved = 저장된_게시글()

        mockMvc.perform(
            patch("/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("""{"id":${saved.id},"title":"고친 제목","content":"고친 내용","name":"무시됨"}"""))
        ).andExpect(status().isOk)

        val updated = boardRepository.findById(saved.id!!).get()
        assert(updated.title == "고친 제목")
        assert(updated.name == "홍길동")
    }

    @Test
    fun `없는 게시글을 수정하면 404 를 반환한다`() {
        mockMvc.perform(
            patch("/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("""{"id":999,"title":"제목","content":"내용","name":"작성자"}"""))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `게시글을 삭제한다`() {
        val saved = 저장된_게시글()

        mockMvc.perform(delete("/board/{id}", saved.id))
            .andExpect(status().isOk)

        assert(boardRepository.findById(saved.id!!).isEmpty)
    }

    @Test
    fun `없는 게시글을 삭제하면 404 를 반환한다`() {
        mockMvc.perform(delete("/board/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `한글이 그대로 오간다`() {
        mockMvc.perform(
            post("/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("""{"title":"한글 제목","content":"한글 내용","name":"홍길동"}"""))
        ).andExpect(status().isOk)

        val saved = boardRepository.findAll().first()

        mockMvc.perform(get("/board/{id}", saved.id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("한글 제목"))
            .andExpect(jsonPath("$.name").value("홍길동"))
    }
}
