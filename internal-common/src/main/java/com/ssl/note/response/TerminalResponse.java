package com.ssl.note.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 10:42
 * @Describe:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminalResponse {
    /**
     * 终端Id
     */
    private String tid;
    /**
     * car的id
     */
    private Long carId;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 经度
     */
    private String longitude;

}
