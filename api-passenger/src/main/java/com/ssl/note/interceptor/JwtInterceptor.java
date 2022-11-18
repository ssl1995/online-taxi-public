package com.ssl.note.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.utils.JwtUtils;
import net.sf.json.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author SongShengLin
 * @date 2022/11/18 22:08
 * @description
 */
public class JwtInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean result = true;
        String resultMsg = "";

        String token = request.getHeader(AUTHORIZATION);
        try {
            JwtUtils.parseToken(token);
        } catch (SignatureVerificationException e) {
            resultMsg = "token sign error";
            result = false;
        } catch (TokenExpiredException e) {
            resultMsg = "token time out";
            result = false;
        } catch (AlgorithmMismatchException e) {
            resultMsg = "token AlgorithmMismatchException";
            result = false;
        } catch (Exception e) {
            resultMsg = "token exception";
            result = false;
        }

        // 如果存在错误，打印错误信息给到前端
        if (!result) {
            PrintWriter out = response.getWriter();
            out.print(JSONObject.fromObject(ResponseResult.fail(resultMsg)).toString());
        }

        return result;
    }
}
