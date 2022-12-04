package com.ssl.note.remote;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.PointRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 17:14
 * @Describe:
 */
@FeignClient(name = "service-map", contextId = "api-driver-service-map-ServiceMapClient")
public interface ServiceMapClient {

    @PostMapping("/point/upload")
    ResponseResult<String> upload(@RequestBody PointRequest pointRequest);

}
