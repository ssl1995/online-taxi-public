package com.ssl.note.service;

import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.response.DirectionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    /**
     * ?origin=116.481028,39.989643&destination=116.465302,40.004717&extensions=all&output=json&key=f7db1c44b0132b46fb2a78cc0f6e25be
     */
    public DirectionResponse direction(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {
        StringBuilder sb = new StringBuilder();
        sb.append(AMapConfigConstants.DIRECTION_URL);
        sb.append("?");
        sb.append("origin=").append(depLongitude).append(",").append(depLatitude);
        sb.append("&");
        sb.append("destination=").append(destLongitude).append(",").append(destLatitude);
        sb.append("&");
        sb.append("extensions=all");
        sb.append("output=json");
        sb.append("&");
        sb.append("key=").append(aMapKey);

        System.out.println(sb.toString());
        return new DirectionResponse();
    }
}
