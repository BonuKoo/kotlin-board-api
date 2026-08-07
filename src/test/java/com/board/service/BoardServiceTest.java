package com.board.service;

import com.board.entity.BoardEntity;
import com.board.entity.dto.BoardDto;
import com.board.exception.BoardNotFoundException;
import com.board.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 서비스 계층의 동작을 실제 H2 로 검증한다.
 *
 * @DataJpaTest 는 JPA 관련 빈만 올리고 각 테스트가 끝나면 롤백한다.
 * 저장소를 흉내 내지 않고 실제로 저장·조회하므로 엔티티 매핑까지 함께 확인된다.
 */
@DataJpaTest
@Import(BoardService.class)
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

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

    @Test
    @DisplayName("게시글을 저장한다")
    void 게시글을_저장한다() {
        boardService.create(new BoardDto(null, "제목", "내용", "작성자"));

        List<BoardEntity> saved = boardRepository.findAll();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getTitle()).isEqualTo("제목");
        assertThat(saved.get(0).getContent()).isEqualTo("내용");
        assertThat(saved.get(0).getName()).isEqualTo("작성자");
    }

    @Test
    @DisplayName("전체 조회는 저장된 게시글을 모두 반환한다")
    void 전체_조회는_저장된_게시글을_모두_반환한다() {
        저장된_게시글("첫 번째");
        저장된_게시글("두 번째");

        List<BoardDto> result = boardService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(BoardDto::getTitle)
                .containsExactlyInAnyOrder("첫 번째", "두 번째");
    }

    @Test
    @DisplayName("게시글이 없으면 전체 조회는 빈 목록을 반환한다")
    void 게시글이_없으면_전체_조회는_빈_목록을_반환한다() {
        assertThat(boardService.getAll()).isEmpty();
    }

    @Test
    @DisplayName("단건 조회는 저장한 값을 그대로 반환한다")
    void 단건_조회는_저장한_값을_그대로_반환한다() {
        BoardEntity saved = 저장된_게시글();

        BoardDto result = boardService.getById(saved.getId());

        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getTitle()).isEqualTo("첫 게시글");
        assertThat(result.getContent()).isEqualTo("내용 A");
        assertThat(result.getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("없는 게시글을 조회하면 예외가 발생한다")
    void 없는_게시글을_조회하면_예외가_발생한다() {
        assertThatThrownBy(() -> boardService.getById(999L))
                .isInstanceOf(BoardNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("수정은 제목과 내용만 바꾸고 작성자는 유지한다")
    void 수정은_제목과_내용만_바꾸고_작성자는_유지한다() {
        BoardEntity saved = 저장된_게시글();

        boardService.update(new BoardDto(saved.getId(), "고친 제목", "고친 내용", "다른 사람"));

        BoardEntity updated = boardRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("고친 제목");
        assertThat(updated.getContent()).isEqualTo("고친 내용");
        assertThat(updated.getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("없는 게시글을 수정하면 예외가 발생한다")
    void 없는_게시글을_수정하면_예외가_발생한다() {
        assertThatThrownBy(() ->
                boardService.update(new BoardDto(999L, "제목", "내용", "작성자")))
                .isInstanceOf(BoardNotFoundException.class);
    }

    @Test
    @DisplayName("id 가 없는 수정 요청은 예외가 발생한다")
    void id_가_없는_수정_요청은_예외가_발생한다() {
        assertThatThrownBy(() ->
                boardService.update(new BoardDto(null, "제목", "내용", "작성자")))
                .isInstanceOf(BoardNotFoundException.class);
    }

    @Test
    @DisplayName("게시글을 삭제한다")
    void 게시글을_삭제한다() {
        BoardEntity saved = 저장된_게시글();

        boardService.deleteById(saved.getId());

        assertThat(boardRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("없는 게시글을 삭제하면 예외가 발생한다")
    void 없는_게시글을_삭제하면_예외가_발생한다() {
        assertThatThrownBy(() -> boardService.deleteById(999L))
                .isInstanceOf(BoardNotFoundException.class);
    }
}
