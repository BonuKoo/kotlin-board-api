package com.board.repository

import com.board.entity.BoardEntity
import org.springframework.data.jpa.repository.JpaRepository


interface BoardRepository : JpaRepository<BoardEntity, Long>{

}