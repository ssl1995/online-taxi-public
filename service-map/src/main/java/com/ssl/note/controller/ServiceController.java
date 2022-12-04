package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.ServiceResponse;
import com.ssl.note.service.ServiceFromMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/03 21:35
 * @Describe:
 */
@RestController
@RequestMapping("/service")
public class ServiceController {

    @Autowired
    private ServiceFromMapService serviceFromMapService;

    @PostMapping("/add")
    public ResponseResult<ServiceResponse> addService(@RequestParam("name") String name) {
        return serviceFromMapService.addService(name);
    }

}
