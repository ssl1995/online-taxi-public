package com.ssl.note.controller;

import com.ssl.note.dto.ResponseResult;
import com.ssl.note.response.TerminalResponse;
import com.ssl.note.response.TrSearchResponse;
import com.ssl.note.service.TerminalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseResult<TerminalResponse> add(@RequestParam("name") String name, @RequestParam("desc") String desc) {
        return terminalService.add(name, desc);
    }

    @PostMapping("/aroundSearch")
    public ResponseResult<List<TerminalResponse>> aroundSearch(@RequestParam("canter") String canter, @RequestParam("radius") String radius) {
        return terminalService.aroundSearch(canter, radius);
    }

    @GetMapping("/trsearch")
    public ResponseResult<TrSearchResponse> trSearch(@RequestParam("tid") String tid, @RequestParam("starttime") Long startTime, @RequestParam("endtime") Long endTime) {
        return terminalService.trSearch(tid, startTime, endTime);
    }

}
