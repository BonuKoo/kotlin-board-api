package com.board.exception;

/**
 * 요청한 게시글이 존재하지 않을 때 발생한다.
 *
 * 이전에는 Optional.get() 이 그대로 NoSuchElementException 을 던져
 * 클라이언트가 500 을 받았다. 존재하지 않는 자원은 서버 오류가 아니라 404 다.
 */
public class BoardNotFoundException extends RuntimeException {

    private final Long id;

    public BoardNotFoundException(Long id) {
        super("게시글을 찾을 수 없습니다. id=" + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
