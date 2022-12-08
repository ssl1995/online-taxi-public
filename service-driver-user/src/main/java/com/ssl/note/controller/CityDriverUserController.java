package com.ssl.note.controller;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.CityDriverUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/08 22:46
 * @Describe:
 */
@RestController
@RequestMapping("/city-driver")
public class CityDriverUserController {

    @Autowired
    private CityDriverUserService cityDriverUserService;

    @GetMapping("/is-available-driver")
    public ResponseResult<Boolean> isAvailableDriver(@RequestParam("cityCode") String cityCode) {
        if (StringUtils.isBlank(cityCode)) {
            return ResponseResult.fail(CommonStatusEnum.PARAM_ERROR.getCode(), "cityCode不能为空");
        }
        return cityDriverUserService.isAvailableDriver(cityCode);
    }

}
