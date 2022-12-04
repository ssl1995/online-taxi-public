package com.ssl.note.remote;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.PointRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 16:35
 * @Describe:
 */
@FeignClient(name = "service-map", contextId = "service-driver-user-PointClient")
@RequestMapping("/point")
public interface PointClient {
    @PostMapping("/upload")
    ResponseResult<String> upload(@RequestBody PointRequest pointRequest);
}
