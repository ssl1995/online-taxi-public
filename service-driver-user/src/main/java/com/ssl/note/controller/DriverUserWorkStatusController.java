package com.ssl.note.controller;


import com.ssl.note.dto.DriverUserWorkStatus;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.service.DriverUserWorkStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DriverUserWorkStatusController {

    @Autowired
    private DriverUserWorkStatusService driverUserWorkStatusService;

    @PostMapping("/driver-user-word-status")
    public ResponseResult<String> changeWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus) {
        return driverUserWorkStatusService.changeWorkStatus(driverUserWorkStatus.getDriverId(), driverUserWorkStatus.getWorkStatus());
    }

}
