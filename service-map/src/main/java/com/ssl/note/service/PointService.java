package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.PointClient;
import com.ssl.note.request.PointRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 16:08
 * @Describe:
 */
@Service
public class PointService {

    @Autowired
    private PointClient pointClient;

    public ResponseResult<String> upload(PointRequest pointRequest){
        return pointClient.upload(pointRequest);
    }
}
