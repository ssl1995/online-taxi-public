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

import java.math.BigDecimal;
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
        BigDecimal price = new BigDecimal(0);

        // 1.计算里程收费
        // 起步价
        Double startFare = priceRule.getStartFare();
        price = price.add(new BigDecimal(startFare));

        // 总里程 km
        BigDecimal distanceBD = new BigDecimal(distance);
        BigDecimal distanceMileDecimal = distanceBD.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);

        // 起步里程
        Integer startMile = priceRule.getStartMile();
        BigDecimal startMileBD = new BigDecimal(startMile);

        // 最终收费的里程数
        double distanceSub = distanceMileDecimal.subtract(startMileBD).doubleValue();
        Double mile = distanceSub < 0 ? 0 : distanceSub;
        BigDecimal mileDB = new BigDecimal(mile);

        // 计程单价
        Double unitPricePerMile = priceRule.getUnitPricePerMile();
        BigDecimal perMileBD = new BigDecimal(unitPricePerMile);

        // 里程价格
        BigDecimal mileFare = mileDB.multiply(perMileBD).setScale(2, BigDecimal.ROUND_UP);
        price = price.add(mileFare);

        // 2.计算时间收费
        // 时长，秒
        BigDecimal time = new BigDecimal(duration);
        BigDecimal timeDB = time.divide(new BigDecimal(60), 2, BigDecimal.ROUND_UP);
        Double unitPricePerMinute = priceRule.getUnitPricePerMinute();
        BigDecimal perMinuteDB = new BigDecimal(unitPricePerMinute);
        BigDecimal timePrice = timeDB.multiply(perMinuteDB);

        // 3.汇总里程和时间收费
        price = price.add(timePrice).setScale(2, BigDecimal.ROUND_UP);

        return price.doubleValue();
    }

//    public static void main(String[] args) {
//        PriceRule priceRule = new PriceRule();
//        priceRule.setUnitPricePerMile(1.8);
//        priceRule.setUnitPricePerMinute(0.5);
//        priceRule.setStartFare(10.0);
//        priceRule.setStartMile(3);
//        System.out.println("getPrice(6500,1800,priceRule) = " + getPrice(6500, 1800, priceRule));
//    }
}
