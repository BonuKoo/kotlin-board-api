package com.board.service;

import com.board.entity.BoardEntity;
import com.board.entity.dto.BoardDto;
import com.board.exception.BoardNotFoundException;
import com.board.repository.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 비즈니스 로직과 Entity ↔ DTO 변환을 담당한다.
 * REST 컨트롤러와 JSP 뷰 컨트롤러가 이 계층을 함께 쓴다.
 */
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public void create(BoardDto boardDto) {
        BoardEntity boardEntity = new BoardEntity(
                boardDto.getTitle(),
                boardDto.getContent(),
                boardDto.getName()
        );
        boardRepository.save(boardEntity);
    }

    // 전체값 조회
    @Transactional(readOnly = true)
    public List<BoardDto> getAll() {
        return boardRepository.findAll().stream()
                .map(BoardService::toDto)
                .toList();
    }

    // 단 건 조회
    @Transactional(readOnly = true)
    public BoardDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    @Transactional
    public void update(BoardDto boardDto) {
        Long id = boardDto.getId();
        if (id == null) {
            throw new BoardNotFoundException(-1L);
        }
        BoardEntity boardEntity = findOrThrow(id);

        boardEntity.update(boardDto);

        // 영속성 컨텍스트가 변경을 감지해 트랜잭션이 끝날 때 반영한다.
        boardRepository.save(boardEntity);
    }

    @Transactional
    public void deleteById(Long id) {
        // 없는 게시글을 지우면 Spring Data 가 던지는 예외 대신 404 로 응답한다.
        if (!boardRepository.existsById(id)) {
            throw new BoardNotFoundException(id);
        }
        boardRepository.deleteById(id);
    }

    private BoardEntity findOrThrow(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException(id));
    }

    private static BoardDto toDto(BoardEntity board) {
        return new BoardDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getName()
        );
    }
}
