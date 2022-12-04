package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.PointClient;
import com.ssl.note.request.PointRequest;
import com.ssl.note.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 16:07
 * @Describe:
 */
@RestController
@RequestMapping("/point")
public class PointController {

    @Autowired
    private PointService pointService;

    @PostMapping("/upload")
    public ResponseResult<String> upload(@RequestBody PointRequest pointRequest) {
        return pointService.upload(pointRequest);
    }
}
