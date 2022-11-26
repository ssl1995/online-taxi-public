package com.ssl.note.remote;

import com.ssl.note.constant.AMapConfigConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 18:08
 * @Describe:
 */
@Service
@Slf4j
public class MapDicDistrictClient {

    @Value("${amap.key}")
    private String key;
    @Autowired
    private RestTemplate restTemplate;

    public String dictDistrict(String keywords) {

        String baseUrl = AMapConfigConstants.DIC_DISTRICT_URL + "?" + "keywords=" + keywords + "&" + "subdistrict=3" + "&" + "key=" + key;
        // 调用高德行政区域查询接口，获得指定城市的省市区信息
        ResponseEntity<String> entity = restTemplate.getForEntity(baseUrl, String.class);

        return entity.getBody();
    }
}
