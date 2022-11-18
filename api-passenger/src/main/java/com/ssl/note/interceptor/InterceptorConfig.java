package com.ssl.note.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author SongShengLin
 * @date 2022/11/18 23:02
 * @description
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                // 拦截的路径
                .addPathPatterns("/*")
                // 不拦截的路径
                .excludePathPatterns("/no-autTest");
    }
}
