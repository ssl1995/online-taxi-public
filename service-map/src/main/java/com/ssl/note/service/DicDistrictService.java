package com.ssl.note.service;

import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.MapDicDistrictClient;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/24 21:25
 * @Describe:
 */
@Service
public class DicDistrictService {

    @Autowired
    private MapDicDistrictClient mapDicDistrictClient;


    public ResponseResult initDicDistrict(String keywords) {

        String dictDistrictJson = mapDicDistrictClient.dictDistrict(keywords);
        JSONObject dicDistrictJsonObj = JSONObject.fromObject(dictDistrictJson);
        int status = (int) dicDistrictJsonObj.get(AMapConfigConstants.STATUS);
        if (status != 1) {
            return ResponseResult.fail(CommonStatusEnum.MAP_DISTRICT_ERROR.getCode(), CommonStatusEnum.MAP_DISTRICT_ERROR.getMessage());
        }



        return ResponseResult.success();
    }

}
