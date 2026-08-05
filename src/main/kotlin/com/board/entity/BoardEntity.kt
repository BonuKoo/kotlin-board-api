package com.board.entity

import com.board.entity.dto.BoardDto
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalTime

@Entity
class BoardEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    var title: String,
    var content: String,
    val name: String,
    val createdAt: LocalTime = LocalTime.now(),
)
{
    open fun update(boardDto: BoardDto){
        this.title = boardDto.title
        this.content = boardDto.content
    }
}

// ID
// Title
// content
// name
// 생성일 etc