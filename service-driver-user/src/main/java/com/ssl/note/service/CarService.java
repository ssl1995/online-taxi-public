package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.Car;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.mapper.CarMapper;
import com.ssl.note.remote.PointClient;
import com.ssl.note.remote.TerminalClient;
import com.ssl.note.remote.TrackClient;
import com.ssl.note.request.PointRequest;
import com.ssl.note.response.TerminalResponse;
import com.ssl.note.response.TrackResponse;
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
public class CarService {

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private TerminalClient terminalClient;

    @Autowired
    private TrackClient trackClient;

    @Autowired
    private PointClient pointClient;

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

        // 上传轨迹

        // 创建车辆
        carMapper.insert(car);

        return ResponseResult.success("");
    }

}
