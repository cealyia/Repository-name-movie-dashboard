package com.ctgu.movie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页跳转：访问 http://localhost:8080/ 时打开看板页面。
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/index.html";
    }
}
