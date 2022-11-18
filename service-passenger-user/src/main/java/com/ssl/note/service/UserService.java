package com.ssl.note.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ssl.note.dto.PassengerUser;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.PassengerUserMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/18 08:17
 * @Describe:
 */
@Service
public class UserService {

    @Autowired
    private PassengerUserMapper passengerUserMapper;


    public ResponseResult loginOrRegUser(String passengerPhone) {
        if (StringUtils.isBlank(passengerPhone)) {
            return ResponseResult.fail(0, "手机号不能为空");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("passenger_phone", passengerPhone);
        map.put("status", 0);
        List<PassengerUser> passengerUsers = passengerUserMapper.selectByMap(map);

        if (CollectionUtils.isEmpty(passengerUsers)) {
            // 插入
            PassengerUser user = PassengerUser
                    .builder()
                    .passengerName("张三")// 可以选择一个随机字符串
                    .passengerGender((byte) 0)
                    .passengerPhone(passengerPhone)
                    .build();
            passengerUserMapper.insert(user);

            return ResponseResult.success();
        }

        System.out.println("用户姓名:" + passengerUsers.get(0).getPassengerName());

        return ResponseResult.success();
    }
}
