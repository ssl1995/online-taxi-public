package com.ssl.note.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/20 16:12
 * @Describe:
 */
@FeignClient(name = "service-sse-push", contextId = "api-driver-service-ServiceSsePushClient")
public interface ServiceSsePushClient {

    @GetMapping("/push")
    String push(@RequestParam Long userId, @RequestParam String identity, @RequestParam String content);
}
