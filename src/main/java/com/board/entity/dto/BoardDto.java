package com.board.entity.dto;

/**
 * 계층 간·클라이언트 간 데이터 전달용.
 *
 * REST 요청의 JSON 바인딩과 JSP 폼의 @ModelAttribute 바인딩에 함께 쓰이므로
 * 기본 생성자와 setter 가 모두 필요하다.
 */
public class BoardDto {

    private Long id;
    private String title = "";
    private String content = "";
    private String name = "";

    public BoardDto() {
    }

    public BoardDto(Long id, String title, String content, String name) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
