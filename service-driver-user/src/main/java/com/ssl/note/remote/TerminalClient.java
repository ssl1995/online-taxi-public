package com.ssl.note.remote;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TerminalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 11:08
 * @Describe:
 */
@FeignClient(name = "service-map", contextId = "service-driver-user-TerminalClient")
public interface TerminalClient {

    @PostMapping("/terminal/add")
    ResponseResult<TerminalResponse> addTerminal(@RequestParam("name") String name);

}
