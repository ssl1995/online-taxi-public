package com.ssl.note.remote;

import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TerminalResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
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

    public ResponseResult<TerminalResponse> add(String name, String desc) {
        String url = AMapConfigConstants.TERMINAL_ADD_URL +
                "?" +
                "key=" + aMapKey +
                "&" +
                "sid=" + aMapSid +
                "&" +
                "name=" + name +
                "&" +
                "desc=" + desc;

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

    public ResponseResult<List<TerminalResponse>> aroundSearch(String canter, String radius) {
        String url = AMapConfigConstants.TERMINAL_AROUND_SEARCH_URL +
                "?" +
                "key=" + aMapKey +
                "&" +
                "sid=" + aMapSid +
                "&" +
                "center=" + canter +
                "&" +
                "radius=" + radius;

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
        JSONArray results = data.getJSONArray("results");

        List<TerminalResponse> terminalResponseList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            TerminalResponse terminalResponse = new TerminalResponse();

            JSONObject jsonObject = results.getJSONObject(i);
            // desc是carId
            String desc = jsonObject.getString("desc");
            Long carId = !StringUtils.equals(desc,"null") ? Long.parseLong(desc) : null;
            String tid = jsonObject.getString("tid");

            JSONObject location = jsonObject.getJSONObject("location");
            String longitude = location.getString("longitude");
            String latitude = location.getString("latitude");

            terminalResponse.setCarId(carId);
            terminalResponse.setTid(tid);
            terminalResponse.setLongitude(longitude);
            terminalResponse.setLatitude(latitude);

            terminalResponseList.add(terminalResponse);
        }

        return ResponseResult.success(terminalResponseList);
    }

}
