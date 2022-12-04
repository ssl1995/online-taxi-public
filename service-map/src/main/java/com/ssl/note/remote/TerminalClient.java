package com.ssl.note.remote;

import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TerminalResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 10:41
 * @Describe:
 */
@Service
@Slf4j
public class TerminalClient {

    @Value("${amap.key}")
    private String aMapKey;

    @Value("${amap.sid}")
    private String aMapSid;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseResult<TerminalResponse> add(String name) {
        String url = AMapConfigConstants.TERMINAL_ADD_URL +
                "?" +
                "key=" + aMapKey +
                "&" +
                "sid=" + aMapSid +
                "&" +
                "name=" + name;

        ResponseEntity<String> forEntity = restTemplate.postForEntity(url, null, String.class);

        String responseBody = forEntity.getBody();
        JSONObject responseJson = JSONObject.fromObject(responseBody);

        int errCode = (int) responseJson.get("errcode");
        // 请求失败
        if (!Objects.equals(errCode, AMapConfigConstants.ERROR_CODE_SUCCESS)) {
            return ResponseResult.fail(CommonStatusEnum.FAIL.getCode(), String.valueOf(responseJson.get("errmsg")));
        }
        // 请求成功
        JSONObject data = responseJson.getJSONObject("data");
        String tid = data.getString("tid");

        TerminalResponse terminalResponse = new TerminalResponse();
        terminalResponse.setTid(tid);

        return ResponseResult.success(terminalResponse);
    }

}
