package com.ssl.note.controller;

import com.alibaba.nacos.common.utils.Objects;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.PriceRule;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.PriceRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/06 22:57
 * @Describe:
 */
@RestController
@RequestMapping("/price-rule")
public class PriceRuleController {

    @Autowired
    private PriceRuleService priceRuleService;

    @PostMapping("/add")
    public ResponseResult<String> add(@RequestBody PriceRule priceRule) {
        return priceRuleService.add(priceRule);
    }

    @PostMapping("/edit")
    public ResponseResult<String> edit(@RequestBody PriceRule priceRule) {
        return priceRuleService.edit(priceRule);
    }

    @GetMapping("/get-newest-version")
    public ResponseResult<PriceRule> getNewestVersion(@RequestParam("fareType") String fareType) {
        return priceRuleService.getNewestVersion(fareType);
    }

    @GetMapping("/is-new")
    public ResponseResult<Boolean> isNew(@RequestParam("fareType") String fareType, @RequestParam("fareVersion") Integer fareVersion) {
        return priceRuleService.isNew(fareType, fareVersion);
    }

}
