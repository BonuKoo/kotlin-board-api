package com.board.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)

/**
 * 컨트롤러 전반에서 발생한 예외를 HTTP 응답으로 옮긴다.
 *
 * 예외 처리를 한 곳에 모아 두면 각 컨트롤러가 정상 흐름만 다루면 된다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BoardNotFoundException::class)
    fun handleBoardNotFound(e: BoardNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    status = HttpStatus.NOT_FOUND.value(),
                    error = HttpStatus.NOT_FOUND.reasonPhrase,
                    message = e.message ?: "게시글을 찾을 수 없습니다.",
                )
            )
}
