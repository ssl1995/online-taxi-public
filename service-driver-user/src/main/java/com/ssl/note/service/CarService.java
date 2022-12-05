package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.Car;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.CarMapper;
import com.ssl.note.remote.TerminalClient;
import com.ssl.note.remote.TrackClient;
import com.ssl.note.response.TerminalResponse;
import com.ssl.note.response.TrackResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
@Slf4j
public class CarService {

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private TerminalClient terminalClient;

    @Autowired
    private TrackClient trackClient;

    public ResponseResult<String> addCar(Car car) {
        LocalDateTime now = LocalDateTime.now();
        car.setGmtCreate(now);
        car.setGmtModified(now);

        // 1.先插入car，获取到cid，然后用于终端请求保存desc = cid
        carMapper.insert(car);
        // 知识点:插入car时，Mybatis会自动生成一个主键Id,赋值给car对象

        // 创建终端,获取tid和保存desc=cid
        ResponseResult<TerminalResponse> terminalResp = terminalClient.addTerminal(car.getVehicleNo(), String.valueOf(car.getId()));
        if (!Objects.equals(terminalResp.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            return ResponseResult.fail(terminalResp.getCode(), terminalResp.getMessage());
        }
        TerminalResponse terminalRespData = terminalResp.getData();
        String tid = terminalRespData.getTid();
        car.setTid(tid);

        // 创建轨迹，获取trid和trname
        ResponseResult<TrackResponse> trackResp = trackClient.addTrack(tid);
        if (!Objects.equals(trackResp.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            return ResponseResult.fail(trackResp.getCode(), trackResp.getMessage());
        }
        TrackResponse trackRespData = trackResp.getData();
        String trid = trackRespData.getTrid();
        car.setTrid(trid);
        if (StringUtils.isNotBlank(trackRespData.getTrname())) {
            car.setTrname(trackRespData.getTrname());
        }

        // 2.更新car
        carMapper.updateById(car);

        return ResponseResult.success("");
    }

    public ResponseResult<Car> getCarById(Long carId) {
        Car car;
        try {
            car = carMapper.selectById(carId);
        } catch (Exception e) {
            return ResponseResult.fail(CommonStatusEnum.FAIL.getCode(), "查询车辆不存在!");
        }
        return ResponseResult.success(car);
    }

}
