package com.ssl.note.remote;

import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TerminalResponse;
import com.ssl.note.response.TrSearchResponse;
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

    /**
     * 新增终端
     */
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


    /**
     * 终端搜索
     */
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
            // desc是carId，不能出错。Json.getLong是会出现经度丢失的，先使用getString再转换为Long
            String desc = jsonObject.getString("desc");
            Long carId = !StringUtils.equals(desc, "null") ? Long.parseLong(desc) : null;
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

    /**
     * 轨迹查询
     */
    public ResponseResult<TrSearchResponse> trSearch(String tid, Long startTime, Long endTime) {
        String url = AMapConfigConstants.TERMINAL_TRSEARCH +
                "?" +
                "key=" + aMapKey +
                "&" +
                "sid=" + aMapSid +
                "&" +
                "tid=" + tid +
                "&" +
                "starttime=" + startTime +
                "&" +
                "endtime=" + endTime;

        ResponseEntity<String> forEntity = restTemplate.postForEntity(url, null, String.class);
        String responseBody = forEntity.getBody();
        JSONObject responseJson = JSONObject.fromObject(responseBody);
        log.info("轨迹查询请求结果:{}", responseJson);

        int errCode = (int) responseJson.get("errcode");
        // 请求失败
        if (!Objects.equals(errCode, AMapConfigConstants.ERROR_CODE_SUCCESS)) {
            return ResponseResult.fail(CommonStatusEnum.FAIL.getCode(), String.valueOf(responseJson.get("errmsg")));
        }

        JSONObject data = responseJson.getJSONObject("data");

        JSONArray tracks = data.getJSONArray("tracks");

        long distance = 0L;
        long time = 0L;
        for (int i = 0; i < tracks.size(); i++) {
            JSONObject trackJson = tracks.getJSONObject(i);
            // 行驶路程，单位米
            distance = trackJson.getLong("distance");
            // 花费时间，单位分钟
            time = Long.parseLong(trackJson.getString("time")) / (1000 * 60);
        }

        TrSearchResponse trSearchResponse = TrSearchResponse.builder()
                .driveMile(distance)
                .driveTime(time)
                .build();
        return ResponseResult.success(trSearchResponse);
    }

}
