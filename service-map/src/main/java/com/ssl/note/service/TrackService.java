package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.TrackClient;
import com.ssl.note.response.TrackResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 14:48
 * @Describe:
 */
@Service
public class TrackService {
    @Autowired
    private TrackClient trackClient;

    public ResponseResult<TrackResponse> addTrack(String tid) {
        return trackClient.add(tid);
    }
}
