package com.ssl.note.remote;

import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.ServiceResponse;
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
 * @Date: 2022/12/03 21:36
 * @Describe: 高德猎鹰-上传轨迹服务请求
 */
@Service
@Slf4j
public class ServiceFromMapClient {
    @Value("${amap.key}")
    private String aMapKey;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 创建服务,获取sid
     */
    public ResponseResult<ServiceResponse> addService(String name) {
        String url = AMapConfigConstants.SERVICE_ADD_URL +
                "?" +
                "key=" + aMapKey +
                "&" +
                "name=" + name;

        ResponseEntity<String> forEntity = restTemplate.postForEntity(url, null, String.class);
        String body = forEntity.getBody();
        JSONObject bodyJson = JSONObject.fromObject(body);

        int errCode = (int) bodyJson.get("errcode");
        // 请求失败
        if (!Objects.equals(errCode, AMapConfigConstants.ERROR_CODE_SUCCESS)) {
            return ResponseResult.fail(errCode, String.valueOf(bodyJson.get("errmsg")));
        }
        // 请求成功
        JSONObject data = bodyJson.getJSONObject("data");
        String sid = data.getString("sid");

        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setSid(sid);

        return ResponseResult.success(serviceResponse);
    }


}
