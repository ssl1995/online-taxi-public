package com.ssl.note.remote;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TrackResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 14:51
 * @Describe:
 */
@FeignClient(name = "service-map", contextId = "service-driver-user-TrackClient")
@RequestMapping("/track")
public interface TrackClient {

    @PostMapping("/add")
    ResponseResult<TrackResponse> addTrack(@RequestParam("tid") String tid);

}
