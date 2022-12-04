package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.DicDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/24 21:24
 * @Describe:
 */
@RestController
public class DistrictController {

    @Autowired
    private DicDistrictService dicDistrictService;

    @GetMapping("/dic-district")
    public ResponseResult initDicDistrict(@RequestParam("keywords") String keywords) {
        return dicDistrictService.initDicDistrict(keywords);
    }
}
