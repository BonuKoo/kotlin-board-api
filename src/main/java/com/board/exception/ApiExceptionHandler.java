package com.board.exception;

import com.board.controller.BoardApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * API 컨트롤러에서 발생한 예외를 JSON 응답으로 옮긴다.
 *
 * 예외 처리를 한 곳에 모아 두면 각 컨트롤러가 정상 흐름만 다루면 된다.
 * 뷰 컨트롤러는 JSON 이 아니라 화면을 돌려줘야 하므로
 * assignableTypes 로 적용 대상을 API 컨트롤러에 한정한다.
 */
@RestControllerAdvice(assignableTypes = BoardApiController.class)
public class ApiExceptionHandler {

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBoardNotFound(BoardNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        e.getMessage() != null ? e.getMessage() : "게시글을 찾을 수 없습니다."
                ));
    }
}
