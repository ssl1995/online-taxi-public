package com.ssl.note.request;

import lombok.Data;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/11 10:55
 * @Describe:
 */
@Data
public class PriceRuleIsNewRequest {
    private String fareType;
    private Integer fareVersion;
}
