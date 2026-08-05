package com.board.service

import com.board.entity.BoardEntity
import com.board.entity.dto.BoardDto
import com.board.repository.BoardRepository
import org.springframework.stereotype.Service

@Service
class BoardService (
    private val boardRepository: BoardRepository
){
    open fun create(
        boardDto: BoardDto
    ){
       val boardEntity =  BoardEntity(
            title = boardDto.title,
            content = boardDto.content,
            name = boardDto.name
        )
        boardRepository.save(boardEntity);
    }

    // 전체값 조회
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
    fun getById(id: Long): BoardDto {
        val boardEntity = boardRepository.findById(id).get()
        val boardDto = BoardDto(
            id = boardEntity.id,
            title = boardEntity.title,
            content = boardEntity.content,
            name = boardEntity.name,
        )
        return boardDto
    }

    fun update(boardDto: BoardDto){
        // !! 는 절대 null 아니라는 선언
        val id : Long = boardDto.id!!
        val boardEntity = boardRepository.findById(id).get()

        boardEntity.update(boardDto)

        //영속성
        boardRepository.save(boardEntity)
    }

    fun deleteById(id: Long){
        boardRepository.deleteById(id)
    }

}