package com.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 루트로 들어온 사람을 게시글 목록으로 보낸다.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/boards";
    }
}
