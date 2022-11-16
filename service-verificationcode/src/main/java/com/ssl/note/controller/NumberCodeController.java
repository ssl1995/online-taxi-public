package com.ssl.note.controller;

import net.sf.json.JSONObject;
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
    public JSONObject getNumberCode(@PathVariable("size") Integer size) {
        System.out.println("size:" + size);

        JSONObject res = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("numberCode", 123456);
        res.put("code", 1);
        res.put("message", "success");
        res.put("data", data);
        return res;
    }
}
