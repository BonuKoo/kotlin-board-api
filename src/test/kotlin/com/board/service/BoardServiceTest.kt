package com.board.service

import com.board.entity.BoardEntity
import com.board.entity.dto.BoardDto
import com.board.exception.BoardNotFoundException
import com.board.repository.BoardRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import

/**
 * 서비스 계층의 동작을 실제 H2 로 검증한다.
 *
 * @DataJpaTest 는 JPA 관련 빈만 올리고 각 테스트가 끝나면 롤백한다.
 * 저장소를 흉내 내지 않고 실제로 저장·조회하므로 엔티티 매핑까지 함께 확인된다.
 */
@DataJpaTest
@Import(BoardService::class)
class BoardServiceTest @Autowired constructor(
    private val boardService: BoardService,
    private val boardRepository: BoardRepository,
) {

    private fun 저장된_게시글(
        title: String = "첫 게시글",
        content: String = "내용 A",
        name: String = "홍길동",
    ): BoardEntity = boardRepository.save(
        BoardEntity(title = title, content = content, name = name)
    )

    @Test
    fun `게시글을 저장한다`() {
        boardService.create(BoardDto(title = "제목", content = "내용", name = "작성자"))

        val saved = boardRepository.findAll()
        assertThat(saved).hasSize(1)
        assertThat(saved[0].title).isEqualTo("제목")
        assertThat(saved[0].content).isEqualTo("내용")
        assertThat(saved[0].name).isEqualTo("작성자")
    }

    @Test
    fun `전체 조회는 저장된 게시글을 모두 반환한다`() {
        저장된_게시글(title = "첫 번째")
        저장된_게시글(title = "두 번째")

        val result = boardService.getAll()

        assertThat(result).hasSize(2)
        assertThat(result.map { it.title }).containsExactlyInAnyOrder("첫 번째", "두 번째")
    }

    @Test
    fun `게시글이 없으면 전체 조회는 빈 목록을 반환한다`() {
        assertThat(boardService.getAll()).isEmpty()
    }

    @Test
    fun `단건 조회는 저장한 값을 그대로 반환한다`() {
        val saved = 저장된_게시글()

        val result = boardService.getById(saved.id!!)

        assertThat(result.id).isEqualTo(saved.id)
        assertThat(result.title).isEqualTo("첫 게시글")
        assertThat(result.content).isEqualTo("내용 A")
        assertThat(result.name).isEqualTo("홍길동")
    }

    @Test
    fun `없는 게시글을 조회하면 예외가 발생한다`() {
        assertThatThrownBy { boardService.getById(999) }
            .isInstanceOf(BoardNotFoundException::class.java)
            .hasMessageContaining("999")
    }

    @Test
    fun `수정은 제목과 내용만 바꾸고 작성자는 유지한다`() {
        val saved = 저장된_게시글()

        boardService.update(
            BoardDto(
                id = saved.id,
                title = "고친 제목",
                content = "고친 내용",
                name = "다른 사람",
            )
        )

        val updated = boardRepository.findById(saved.id!!).get()
        assertThat(updated.title).isEqualTo("고친 제목")
        assertThat(updated.content).isEqualTo("고친 내용")
        assertThat(updated.name).isEqualTo("홍길동")
    }

    @Test
    fun `없는 게시글을 수정하면 예외가 발생한다`() {
        assertThatThrownBy {
            boardService.update(BoardDto(id = 999, title = "제목", content = "내용", name = "작성자"))
        }.isInstanceOf(BoardNotFoundException::class.java)
    }

    @Test
    fun `id 가 없는 수정 요청은 예외가 발생한다`() {
        assertThatThrownBy {
            boardService.update(BoardDto(id = null, title = "제목", content = "내용", name = "작성자"))
        }.isInstanceOf(BoardNotFoundException::class.java)
    }

    @Test
    fun `게시글을 삭제한다`() {
        val saved = 저장된_게시글()

        boardService.deleteById(saved.id!!)

        assertThat(boardRepository.findAll()).isEmpty()
    }

    @Test
    fun `없는 게시글을 삭제하면 예외가 발생한다`() {
        assertThatThrownBy { boardService.deleteById(999) }
            .isInstanceOf(BoardNotFoundException::class.java)
    }
}
