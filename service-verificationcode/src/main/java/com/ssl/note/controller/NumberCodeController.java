package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.NumberCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/16 09:41
 * @Describe:
 */
@RestController
public class NumberCodeController {

    @GetMapping("/numberCode/{size}")
    public ResponseResult<NumberCodeResponse> getNumberCode(@PathVariable("size") Integer size) {
        System.out.println("size:" + size);

        NumberCodeResponse data = new NumberCodeResponse();
        data.setNumberCode(getRandomSixNum(size));

        return ResponseResult.success(data);
    }

    private Integer getRandomSixNum(Integer size) {
        return (int) ((Math.random() * 9 + 1) * Math.pow(10, size - 1));
    }
}
