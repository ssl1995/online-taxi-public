package com.ssl.note.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/18 17:24
 * @Describe:
 */
@FeignClient(name = "service-sse-push", contextId = "service-order-SsePushClient")
public interface SsePushClient {
    @GetMapping("/push")
    String push(@RequestParam Long userId, @RequestParam String identity, @RequestParam String content);
}
