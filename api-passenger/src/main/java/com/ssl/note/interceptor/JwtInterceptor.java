package com.ssl.note.interceptor;

import com.ssl.note.constant.HeaderConstant;
import com.ssl.note.constant.TokenConstant;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.dto.TokenResult;
import com.ssl.note.utils.JwtUtils;
import com.ssl.note.utils.RedisPrefixUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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
        // 1.请求头获取Authorization
        String token = request.getHeader(HeaderConstant.AUTHORIZATION);
        // 2.解析token
        TokenResult tokenResult = JwtUtils.checkToken(token);

        boolean result = true;
        String failMsg = "";
        // 3.token解析失败,记录错误
        if (Objects.isNull(tokenResult)) {
            failMsg = "access check error";
            result = false;
        } else {
            String phone = tokenResult.getPhone();
            String identity = tokenResult.getIdentity();
            // 4. 从Redis中取出存的token
            String tokenKey = RedisPrefixUtils.generateTokenKey(phone, identity, TokenConstant.ACCESS_TOKEN_TYPE);
            String tokenRedis = stringRedisTemplate.opsForValue().get(tokenKey);
            // 5. 不存在，或者不相等,记录错误
            if (StringUtils.isBlank(tokenKey) || !StringUtils.equals(Objects.requireNonNull(tokenRedis).trim(), token.trim())) {
                failMsg = "access token invalid";
                result = false;
            }
        }
        // 6.如果存在错误，打印错误信息给到前端
        if (!result) {
            PrintWriter out = response.getWriter();
            out.print(JSONObject.fromObject(ResponseResult.fail(failMsg)).toString());
        }
        // 7.返回结果
        return result;
    }
}
