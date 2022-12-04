package com.ssl.note.service;

import com.ssl.note.constant.AMapConfigConstants;
import com.ssl.note.constant.CommonStatusEnum;
import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.TerminalClient;
import com.ssl.note.response.TerminalResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 10:46
 * @Describe:
 */
@Service
public class TerminalService {

    @Autowired
    private TerminalClient terminalClient;

    public ResponseResult<TerminalResponse> add(String name, String desc) {
        return terminalClient.add(name, desc);
    }

    public ResponseResult<List<TerminalResponse>> aroundSearch(String canter, String radius) {
        return terminalClient.aroundSearch(canter, radius);
    }

}
