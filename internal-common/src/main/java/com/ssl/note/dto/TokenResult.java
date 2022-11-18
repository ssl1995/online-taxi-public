package com.ssl.note.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author SongShengLin
 * @date 2022/11/18 21:49
 * @description
 */
@Data
@Builder
public class TokenResult {

    private String phone;

    private String identity;
}
