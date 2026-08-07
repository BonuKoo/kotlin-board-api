package com.board.exception;

import com.board.controller.BoardViewController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * 뷰 컨트롤러에서 발생한 예외를 에러 화면으로 옮긴다.
 * 브라우저에는 JSON 대신 사람이 읽을 수 있는 HTML 을 돌려준다.
 */
@ControllerAdvice(assignableTypes = BoardViewController.class)
public class ViewExceptionHandler {

    @ExceptionHandler(BoardNotFoundException.class)
    public ModelAndView handleBoardNotFound(BoardNotFoundException e) {
        ModelAndView mav = new ModelAndView("error/notFound");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", e.getMessage());
        return mav;
    }
}
