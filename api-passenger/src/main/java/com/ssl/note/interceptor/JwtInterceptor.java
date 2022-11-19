package com.ssl.note.interceptor;

import com.ssl.note.constant.HeaderConstant;
import com.ssl.note.constant.TokenConstant;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.dto.TokenResult;
import com.ssl.note.utils.JwtUtils;
import com.ssl.note.utils.RedisPrefixUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * @author SongShengLin
 * @date 2022/11/18 22:08
 * @description
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader(HeaderConstant.AUTHORIZATION);
        TokenResult tokenResult = JwtUtils.checkToken(token);

        boolean result = true;
        String failMsg = "";
        // 从redis中取出token
        if (Objects.isNull(tokenResult)) {
            failMsg = "access token invalid";
            result = false;
        } else {
            String phone = tokenResult.getPhone();
            String identity = tokenResult.getIdentity();

            String tokenKey = RedisPrefixUtils.generateTokenKey(phone, identity, TokenConstant.ACCESS_TOKEN_TYPE);
            String tokenRedis = stringRedisTemplate.opsForValue().get(tokenKey);
            if (StringUtils.isBlank(tokenKey) || !StringUtils.equals(Objects.requireNonNull(tokenRedis).trim(), token.trim())) {
                failMsg = "access token invalid";
                result = false;
            }
        }


        // 如果存在错误，打印错误信息给到前端
        if (!result) {
            PrintWriter out = response.getWriter();
            out.print(JSONObject.fromObject(ResponseResult.fail(failMsg)).toString());
        }

        return result;
    }
}
