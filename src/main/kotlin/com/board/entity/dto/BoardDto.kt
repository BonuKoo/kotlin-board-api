package com.board.entity.dto

import java.time.LocalTime

data class BoardDto (
    val id: Long? = null,
    var title: String = "",
    var content: String = "",
    val name: String = "",
)