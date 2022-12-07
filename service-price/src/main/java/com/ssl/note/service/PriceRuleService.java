package com.ssl.note.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.PriceRule;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.PriceRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/06 22:57
 * @Describe:
 */
@Service
public class PriceRuleService {

    @Autowired
    private PriceRuleMapper priceRuleMapper;

    public ResponseResult<String> add(PriceRule priceRule) {
        String cityCode = priceRule.getCityCode();
        String vehicleType = priceRule.getVehicleType();
        String fareType = cityCode + "$" + vehicleType;

        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> priceRules = priceRuleMapper.selectList(queryWrapper);
        Integer fareVersion = 0;
        if (!CollectionUtils.isEmpty(priceRules)) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EXISTS.getCode(),
                    CommonStatusEnum.PRICE_RULE_EXISTS.getMessage());
        }

        priceRule.setFareType(fareType);
        priceRule.setFareVersion(++fareVersion);

        priceRuleMapper.insert(priceRule);

        return ResponseResult.success("");
    }

    public ResponseResult<String> edit(PriceRule priceRule) {
        String cityCode = priceRule.getCityCode();
        String vehicleType = priceRule.getVehicleType();
        String fareType = cityCode + "$" + vehicleType;

        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> priceRules = priceRuleMapper.selectList(queryWrapper);
        Integer fareVersion = 0;
        if (!CollectionUtils.isEmpty(priceRules)) {
            PriceRule lastPriceRule = priceRules.get(0);
            if (!checkChangedPrice(lastPriceRule, priceRule)) {
                return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_NOT_EDIT.getCode(),
                        CommonStatusEnum.PRICE_RULE_NOT_EDIT.getMessage());
            }
            fareVersion = lastPriceRule.getFareVersion();
        }

        priceRule.setFareType(fareType);
        priceRule.setFareVersion(++fareVersion);

        priceRuleMapper.insert(priceRule);

        return ResponseResult.success("");
    }

    private Boolean checkChangedPrice(PriceRule lastPriceRule, PriceRule editPriceRule) {

        if (!Objects.equals(BigDecimal.valueOf(lastPriceRule.getStartFare()).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(editPriceRule.getStartFare()).setScale(2, RoundingMode.HALF_UP))
                || !Objects.equals(lastPriceRule.getStartMile(), editPriceRule.getStartMile())
                || !Objects.equals(BigDecimal.valueOf(lastPriceRule.getUnitPricePerMile()).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(editPriceRule.getUnitPricePerMile()).setScale(2, RoundingMode.HALF_UP))
                || !Objects.equals(BigDecimal.valueOf(lastPriceRule.getUnitPricePerMinute()).setScale(2, RoundingMode.HALF_UP), BigDecimal.valueOf(editPriceRule.getUnitPricePerMinute()).setScale(2, RoundingMode.HALF_UP))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public ResponseResult<PriceRule> getNewestVersion(String fareType) {
        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fare_type", fareType);
        queryWrapper.orderByDesc("fare_version");
        List<PriceRule> priceRules = priceRuleMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(priceRules)) {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(), CommonStatusEnum.PRICE_RULE_EMPTY.getMessage());
        }
        return ResponseResult.success(priceRules.get(0));
    }

    public ResponseResult<Boolean> isNew(String fareType, Integer fareVersion) {
        ResponseResult<PriceRule> priceRulesResp = getNewestVersion(fareType);
        if (!Objects.equals(priceRulesResp.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            return ResponseResult.fail(priceRulesResp.getCode(), priceRulesResp.getMessage());
        }
        PriceRule priceRule = priceRulesResp.getData();
        Integer fareVersionDB = priceRule.getFareVersion();
        if (ObjectUtils.isEmpty(fareVersionDB) || fareVersion < fareVersionDB) {
            return ResponseResult.success(Boolean.FALSE);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

}
