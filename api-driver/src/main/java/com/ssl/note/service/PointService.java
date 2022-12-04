package com.ssl.note.service;

import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.Car;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.ServiceDriverUserClient;
import com.ssl.note.remote.ServiceMapClient;
import com.ssl.note.request.ApiDriverPointRequest;
import com.ssl.note.request.PointRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 17:02
 * @Describe:
 */
@Service
public class PointService {

    @Autowired
    private ServiceDriverUserClient driverUserClient;

    @Autowired
    private ServiceMapClient mapClient;

    public ResponseResult<String> upload(ApiDriverPointRequest driverPointRequest) {
        // 1.通过carId查询tid、trid
        Long carId = driverPointRequest.getCarId();
        ResponseResult<Car> carResp = driverUserClient.getCarById(carId);
        if (!Objects.equals(carResp.getCode(), CommonStatusEnum.SUCCESS.getCode())) {
            return ResponseResult.fail(carResp.getCode(), carResp.getMessage());
        }
        Car car = carResp.getData();
        String tid = car.getTid();
        String trid = car.getTrid();

        // 2.通过tid、trid、points上传轨迹
        PointRequest pointReq = PointRequest.builder()
                .tid(tid)
                .trid(trid)
                .points(driverPointRequest.getPoints())
                .build();
        return mapClient.upload(pointReq);
    }

}
