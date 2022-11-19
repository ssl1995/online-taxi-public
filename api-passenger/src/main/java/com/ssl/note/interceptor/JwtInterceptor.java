package com.ssl.note.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ssl.note.constant.TokenConstants;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.dto.TokenResult;
import com.ssl.note.utils.JwtUtils;
import com.ssl.note.utils.RedisPrefixUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }


    public static final String AUTHORIZATION = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean result = true;

        String token = request.getHeader(AUTHORIZATION);
        TokenResult tokenResult = new TokenResult();
        String failMsg = "";
        try {
            tokenResult = JwtUtils.parseToken(token);
        } catch (SignatureVerificationException e) {
            failMsg = "token sign error";
            result = false;
        } catch (TokenExpiredException e) {
            failMsg = "token time out";
            result = false;
        } catch (AlgorithmMismatchException e) {
            failMsg = "token AlgorithmMismatchException";
            result = false;
        } catch (Exception e) {
            failMsg = "token invalid";
            result = false;
        }

        // 从redis中取出token
        if (Objects.isNull(tokenResult)) {
            failMsg = "token invalid";
            result = false;
        } else {
            String phone = tokenResult.getPhone();
            String identity = tokenResult.getIdentity();

            String tokenKey = RedisPrefixUtils.generateTokenKey(phone, identity, TokenConstants.ACCESS_TOKEN_TYPE);
            if (!StringUtils.isBlank(tokenKey)
                    || !StringUtils.equals(tokenKey.trim(), token.trim())) {
                failMsg = "token invalid";
                result = false;
            }

            stringRedisTemplate.opsForValue().set(phone,tokenKey);
        }


        // 如果存在错误，打印错误信息给到前端
        if (!result) {
            PrintWriter out = response.getWriter();
            out.print(JSONObject.fromObject(ResponseResult.fail(failMsg)).toString());
        }

        return result;
    }
}
