package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.PriceRule;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.PriceRuleMapper;
import com.ssl.note.remote.ServiceMapClient;
import com.ssl.note.request.ForecastPriceDTO;
import com.ssl.note.response.DirectionResponse;
import com.ssl.note.response.ForecastPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/21 09:44
 * @Describe:
 */
@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    private PriceRuleMapper priceRuleMapper;

    /**
     * 获取预估价格
     */
    public ResponseResult<ForecastPriceResponse> forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude) {
        ForecastPriceDTO forecastPriceDTO = ForecastPriceDTO.builder()
                .depLongitude(depLongitude)
                .depLatitude(depLatitude)
                .destLongitude(destLongitude)
                .destLatitude(destLatitude)
                .build();
        // 1.获取距离和时长
        ResponseResult<DirectionResponse> directionResp = serviceMapClient.dirving(forecastPriceDTO);
        DirectionResponse directionData = directionResp.getData();

        Integer distance = directionData.getDistance();
        Integer duration = directionData.getDuration();

        // 2.获取计价规则
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("city_code", "110000");
        queryMap.put("vehicle_type", "1");
        List<PriceRule> priceRules = priceRuleMapper.selectByMap(queryMap);
        if (CollectionUtils.isEmpty(priceRules)) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(), CommonStatusEnum.PRICE_RULE_EMPTY.getMessage());
        }
        PriceRule priceRule = priceRules.get(0);



        ForecastPriceResponse resp = new ForecastPriceResponse();
        resp.setPrice(12.13);
        return ResponseResult.success(resp);
    }
}
