package com.ssl.note.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SongShengLin
 * @date 2022/11/14 21:58
 * @description
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test api";
    }
}
