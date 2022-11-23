package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.PriceRule;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.PriceRuleMapper;
import com.ssl.note.remote.ServiceMapClient;
import com.ssl.note.request.ForecastPriceDTO;
import com.ssl.note.response.DirectionResponse;
import com.ssl.note.response.ForecastPriceResponse;
import com.ssl.note.utils.BigDecimalUtils;
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

        // 3.计算预估价格
        Double price = getPrice(distance, duration, priceRule);

        ForecastPriceResponse resp = new ForecastPriceResponse();
        resp.setPrice(price);
        return ResponseResult.success(resp);
    }


    /**
     * 根据距离、时长、计价规则、计算最终价格
     */
    private Double getPrice(Integer distance, Integer duration, PriceRule priceRule) {
        // 1.计算里程收费
        // 起步价
        Double startFare = priceRule.getStartFare();

        // 总里程 km
        double distanceMile = BigDecimalUtils.divide(distance, 1000);
        // 起步里程
        double startMile = (double) priceRule.getStartMile();

        // 最终收费的里程数
        double distanceSub = BigDecimalUtils.subtract(distanceMile, startMile);
        double mile = distanceSub < 0 ? 0 : distanceSub;

        // 计程单价
        Double unitPricePerMile = priceRule.getUnitPricePerMile();

        // 里程价格
        double mileFare = BigDecimalUtils.multiply(mile, unitPricePerMile);

        // 2.计算时间收费
        // 时长，秒
        double time = BigDecimalUtils.divide(duration, 60);
        double unitPricePerMinute = priceRule.getUnitPricePerMinute();

        double timePrice = BigDecimalUtils.multiply(time, unitPricePerMinute);

        // 3.汇总里程和时间收费
        double price;
        price = BigDecimalUtils.add(0, startFare);
        price = BigDecimalUtils.add(price, mileFare);
        price = BigDecimalUtils.add(price, timePrice);

        return price;
    }
}
