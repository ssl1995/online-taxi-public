package com.ssl.note.remote;

import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.PointDTO;
import com.ssl.note.request.PointRequest;
import com.ssl.note.response.TrackResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 16:07
 * @Describe:
 */
@Service
@Slf4j
public class PointClient {
    @Value("${amap.key}")
    private String aMapKey;

    @Value("${amap.sid}")
    private String aMapSid;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseResult<String> upload(PointRequest pointRequest) {
        String url = AMapConfigConstants.POINT_UPLOAD_URL +
                "?" +
                "key=" + aMapKey +
                "&" +
                "sid=" + aMapSid +
                "&" +
                "tid=" + pointRequest.getTid() +
                "&" +
                "trid=" + pointRequest.getTrid() +
                "&" +
                "points=" + getPointsJson(pointRequest.getPoints());
        // 难点：这里会出现各种转义的问题
        // URI.create(url)将特殊%5B等转义
        ResponseEntity<String> forEntity = restTemplate.postForEntity(URI.create(url), null, String.class);

        String responseBody = forEntity.getBody();
        JSONObject responseJson = JSONObject.fromObject(responseBody);

        int errCode = (int) responseJson.get("errcode");
        // 请求失败
        if (!Objects.equals(errCode, AMapConfigConstants.ERROR_CODE_SUCCESS)) {
            log.error("上传轨迹失败，请求url={}，请求结果={}", url, responseJson);
            return ResponseResult.fail(CommonStatusEnum.FAIL.getCode(), String.valueOf(responseJson.get("errmsg")));
        }

        // 请求成功
        log.info("上传轨迹成功，请求url={}，请求结果={}", url, responseJson);
        return ResponseResult.success("");
    }

    private String getPointsJson(PointDTO[] points) {
        StringBuilder url = new StringBuilder();
        url.append("%5B");
        for (PointDTO p : points) {
            url.append("%7B");
            String locatetime = p.getLocatetime();
            String location = p.getLocation();
            url.append("%22location%22");
            url.append("%3A");
            url.append("%22").append(location).append("%22");
            url.append("%2C");

            url.append("%22locatetime%22");
            url.append("%3A");
            url.append(locatetime);

            url.append("%7D");
        }
        url.append("%5D");
        return url.toString();
    }

    public static void main(String[] args) {
        PointDTO pointDTO = new PointDTO();
        pointDTO.setLocation("116.347689,40.007473");
        pointDTO.setLocatetime("1670073581641");
        PointDTO[] points = new PointDTO[1];
        points[0] = pointDTO;

        PointClient pointClient = new PointClient();
        System.out.println(pointClient.getPointsJson(points));
    }


}
