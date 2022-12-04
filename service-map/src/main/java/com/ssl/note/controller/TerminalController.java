package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TerminalResponse;
import com.ssl.note.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 10:49
 * @Describe:
 */
@RestController
@RequestMapping("/terminal")
public class TerminalController {

    @Autowired
    private TerminalService terminalService;

    @PostMapping("/add")
    public ResponseResult<TerminalResponse> add(@RequestParam("name") String name) {
        return terminalService.add(name);
    }

}
