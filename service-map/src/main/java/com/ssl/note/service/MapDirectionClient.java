package com.ssl.note.service;

import ch.qos.logback.core.status.StatusUtil;
import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.response.DirectionResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 18:08
 * @Describe:
 */
@Service
@Slf4j
public class MapDirectionClient {

    @Value("${amap.key}")
    private String aMapKey;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * ?origin=116.481028,39.989643&destination=116.465302,40.004717&extensions=all&output=json&key=f7db1c44b0132b46fb2a78cc0f6e25be
     */
    public DirectionResponse direction(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {
        String urlBaseBuilder = AMapConfigConstants.DIRECTION_URL +
                "?" +
                "origin=" + depLongitude + "," + depLatitude +
                "&" +
                "destination=" + destLongitude + "," + destLatitude +
                "&" +
                "extensions=base" +
                "output=json" +
                "&" +
                "key=" + aMapKey;

        // restTemplate调用高德路线规划的接口
        ResponseEntity<String> entity = restTemplate.getForEntity(urlBaseBuilder, String.class);
        System.out.println("body:"+entity.getBody());

        DirectionResponse directionResponse = parseDirectionEntity(entity.getBody());

        return directionResponse;
    }

    private DirectionResponse parseDirectionEntity(String direction) {
        DirectionResponse directionResponse = null;
        try {
            JSONObject directionJson = JSONObject.fromObject(direction);
            if (directionJson.has(AMapConfigConstants.STATUS)) {
                Integer status = directionJson.getInt(AMapConfigConstants.STATUS);
                if (Objects.equals(status, 1)) {
                    if (directionJson.has(AMapConfigConstants.ROUTE)) {
                        JSONObject routeJson = directionJson.getJSONObject(AMapConfigConstants.ROUTE);
                        if (routeJson.has(AMapConfigConstants.PATHS)) {
                            JSONArray pathArrayJson = routeJson.getJSONArray(AMapConfigConstants.PATHS);
                            // 取数组第一个为"速度最快"
                            JSONObject pathJson = pathArrayJson.getJSONObject(0);
                            directionResponse = new DirectionResponse();
                            if (pathJson.has(AMapConfigConstants.DISTANCE)) {
                                int distance = pathJson.getInt(AMapConfigConstants.DISTANCE);
                                directionResponse.setDistance(distance);
                            }
                            if (pathJson.has(AMapConfigConstants.DURATION)) {
                                int duration = pathJson.getInt(AMapConfigConstants.DURATION);
                                directionResponse.setDuration(duration);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
        return directionResponse;
    }
}
