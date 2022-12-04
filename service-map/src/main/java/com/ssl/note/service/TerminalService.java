package com.ssl.note.service;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.remote.TerminalClient;
import com.ssl.note.response.TerminalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 10:46
 * @Describe:
 */
@Service
public class TerminalService {

    @Autowired
    private TerminalClient terminalClient;

    public ResponseResult<TerminalResponse> add(String name) {
        return terminalClient.add(name);
    }

}
