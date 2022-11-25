package com.ssl.note.service;

import com.ssl.note.dto.DriverUser;
import com.ssl.note.mapper.DriverUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/25 21:22
 * @Describe:
 */
@Service
public class DriverUserService {

    @Autowired
    private DriverUserMapper driverUserMapper;

    public DriverUser getTest(Long id){

        HashMap<String, Object> map = new HashMap<>();
        map.put("id",id);
        List<DriverUser> driverUsers = driverUserMapper.selectByMap(map);
        return driverUsers.get(0);
    }


}
