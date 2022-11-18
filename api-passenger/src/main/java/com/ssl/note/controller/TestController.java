package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author SongShengLin
 * @date 2022/11/18 22:05
 * @description
 */
@RestController
public class TestController {

    @GetMapping("/autTest")
    public ResponseResult<String> authTest() {
        return ResponseResult.success("auth test");
    }


    @GetMapping("/no-autTest")
    public ResponseResult<String> noAuthTest() {
        return ResponseResult.success("no-autTest");
    }

}
