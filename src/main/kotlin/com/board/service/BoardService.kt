package com.board.service

import com.board.entity.BoardEntity
import com.board.entity.dto.BoardDto
import com.board.exception.BoardNotFoundException
import com.board.repository.BoardRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardService (
    private val boardRepository: BoardRepository
){
    @Transactional
    fun create(
        boardDto: BoardDto
    ){
       val boardEntity =  BoardEntity(
            title = boardDto.title,
            content = boardDto.content,
            name = boardDto.name
        )
        boardRepository.save(boardEntity)
    }

    // 전체값 조회
    @Transactional(readOnly = true)
    fun getAll(): List<BoardDto>{
        val boardEntities = boardRepository.findAll()
        val boardDtoList = boardEntities.map { board ->
            BoardDto(
                id = board.id,
                title = board.title,
                content = board.content,
                name = board.name
            )
        }
        return boardDtoList
    }

    // 단 건 조회
    @Transactional(readOnly = true)
    fun getById(id: Long): BoardDto {
        val boardEntity = findOrThrow(id)
        val boardDto = BoardDto(
            id = boardEntity.id,
            title = boardEntity.title,
            content = boardEntity.content,
            name = boardEntity.name,
        )
        return boardDto
    }

    @Transactional
    fun update(boardDto: BoardDto){
        val id = boardDto.id ?: throw BoardNotFoundException(-1)
        val boardEntity = findOrThrow(id)

        boardEntity.update(boardDto)

        // 영속성 컨텍스트가 변경을 감지해 트랜잭션이 끝날 때 반영한다.
        boardRepository.save(boardEntity)
    }

    @Transactional
    fun deleteById(id: Long){
        // 없는 게시글을 지우면 Spring Data 가 던지는 예외 대신 404 로 응답한다.
        if (!boardRepository.existsById(id)) throw BoardNotFoundException(id)
        boardRepository.deleteById(id)
    }

    private fun findOrThrow(id: Long): BoardEntity =
        boardRepository.findById(id).orElseThrow { BoardNotFoundException(id) }
}
