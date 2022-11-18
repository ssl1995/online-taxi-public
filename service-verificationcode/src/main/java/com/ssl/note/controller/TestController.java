package com.ssl.note.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/16 09:15
 * @Describe:
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String getTest() {
        return "hello service-verificationCode";
    }
}
