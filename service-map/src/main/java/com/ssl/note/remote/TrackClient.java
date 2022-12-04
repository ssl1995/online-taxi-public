package com.ssl.note.remote;

import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TerminalResponse;
import com.ssl.note.response.TrackResponse;
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
 * @Date: 2022/12/04 14:43
 * @Describe:
 */
@Service
@Slf4j
public class TrackClient {
    @Value("${amap.key}")
    private String aMapKey;

    @Value("${amap.sid}")
    private String aMapSid;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseResult<TrackResponse> add(String tid) {
        String url = AMapConfigConstants.TRACK_ADD_URL +
                "?" +
                "key=" + aMapKey +
                "&" +
                "sid=" + aMapSid +
                "&" +
                "tid=" + tid;

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
        String trid = data.getString("trid");
        String trname = data.has("trname") ? data.getString("trname") : null;

        TrackResponse trackResponse = TrackResponse
                .builder()
                .trid(trid)
                .trname(trname)
                .build();

        return ResponseResult.success(trackResponse);
    }

}
