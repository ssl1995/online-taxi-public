package com.ssl.note.service;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @author SongShengLin
 * @date 2022/11/14 22:17
 * @description
 */
@Service
public class VerificationCodeService {

    public String getVerificationCode(String passengerPhone) {
        System.out.println("1");


        JSONObject result = new JSONObject();
        result.put("code", 1);
        result.put("message", "success");

        return result.toString();
    }
}
