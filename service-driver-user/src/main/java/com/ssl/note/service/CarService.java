package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.Car;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.CarMapper;
import com.ssl.note.remote.TerminalClient;
import com.ssl.note.response.TerminalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/28 22:45
 * @Describe:
 */
@Service
public class CarService {

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private TerminalClient terminalClient;

    public ResponseResult<String> saveCar(Car car) {
        LocalDateTime now = LocalDateTime.now();
        car.setGmtCreate(now);
        car.setGmtModified(now);

        // 创建终端,获取tid
        ResponseResult<TerminalResponse> terminalResp = terminalClient.addTerminal(car.getVehicleNo());
        if (!Objects.equals(terminalResp.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            return ResponseResult.fail(terminalResp.getCode(), terminalResp.getMessage());
        }
        TerminalResponse terminalRespData = terminalResp.getData();
        String tid = terminalRespData.getTid();
        car.setTid(tid);

        // 创建车辆
        carMapper.insert(car);

        return ResponseResult.success("");
    }

}
