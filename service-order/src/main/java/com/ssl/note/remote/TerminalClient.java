package com.ssl.note.remote;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TerminalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/09 14:47
 * @Describe:
 */
@FeignClient(name = "service-map", contextId = "service-map-TerminalClient")
public interface TerminalClient {

    @PostMapping("/terminal/aroundSearch")
    ResponseResult<List<TerminalResponse>> aroundSearch(@RequestParam("canter") String canter, @RequestParam("radius") String radius);
}
