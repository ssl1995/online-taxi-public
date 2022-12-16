package com.ssl.note.controller;

import com.ssl.note.utils.SsePrefixUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/13 15:35
 * @Describe:
 */
@RestController
@Slf4j
public class SseController {

    public static Map<String, SseEmitter> sseEmitterMap = new HashMap<>();

    /**
     * 建立连接
     *
     * @param userId   用户id
     * @param identity 身份类型
     * @return
     */
    @GetMapping("/connect")
    public SseEmitter connect(@RequestParam Long userId, @RequestParam String identity) {
        log.info("用户ID：" + userId + ",身份类型：" + identity);
        // 建立一个连接
        SseEmitter sseEmitter = new SseEmitter(0l);

        String sseMapKey = SsePrefixUtils.generatorSseKey(userId, identity);

        sseEmitterMap.put(sseMapKey, sseEmitter);

        return sseEmitter;
    }

    /**
     * 发送消息
     *
     * @param userId   用户id
     * @param identity 身份类型
     * @param content  内容
     * @return
     */
    @GetMapping("/push")
    public String push(@RequestParam Long userId, @RequestParam String identity, @RequestParam String content) {
        log.info("用户ID：" + userId + ",身份：" + identity);
        String sseMapKey = SsePrefixUtils.generatorSseKey(userId, identity);
        try {
            if (sseEmitterMap.containsKey(sseMapKey)) {
                sseEmitterMap.get(sseMapKey).send(content);
            } else {
                return "推送失败";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "给用户：" + sseMapKey + ",发送了消息：" + content;
    }

    /**
     * 关闭连接
     *
     * @param userId   用户ID
     * @param identity 身份类型
     * @return
     */
    @GetMapping("/close")
    public String close(@RequestParam Long userId, @RequestParam String identity) {
        String sseMapKey = SsePrefixUtils.generatorSseKey(userId, identity);
        System.out.println("关闭连接：" + sseMapKey);
        sseEmitterMap.remove(sseMapKey);
        return "close 成功";

    }
}
