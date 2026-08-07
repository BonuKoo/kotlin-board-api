package com.board.entity;

import com.board.entity.dto.BoardDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalTime;

@Entity
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String name;

    private LocalTime createdAt;

    /**
     * JPA 가 리플렉션으로 인스턴스를 만들 때만 쓴다.
     * Kotlin 에서는 plugin.jpa 가 이 생성자를 자동으로 만들어 주었다.
     */
    protected BoardEntity() {
    }

    public BoardEntity(String title, String content, String name) {
        this.title = title;
        this.content = content;
        this.name = name;
        this.createdAt = LocalTime.now();
    }

    /**
     * 제목과 내용만 바꾼다. 작성자는 한 번 정해지면 바뀌지 않는다.
     */
    public void update(BoardDto boardDto) {
        this.title = boardDto.getTitle();
        this.content = boardDto.getContent();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public LocalTime getCreatedAt() {
        return createdAt;
    }
}
