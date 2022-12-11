package com.ssl.note.remote;

import com.ssl.note.dto.PriceRule;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.request.PriceRuleIsNewRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/07 22:26
 * @Describe:
 */
@FeignClient(name = "service-price", contextId = "service-order-ServicePriceClient")
public interface ServicePriceClient {

    @PostMapping("/price-rule/is-new")
    ResponseResult<Boolean> isNew(@RequestBody PriceRuleIsNewRequest priceRuleIsNewRequest);

    @GetMapping("/price-rule/is-exists")
    ResponseResult<Boolean> isExists(@RequestBody PriceRule priceRule);
}
