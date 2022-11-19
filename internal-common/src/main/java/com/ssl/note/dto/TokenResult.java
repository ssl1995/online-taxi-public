package com.ssl.note.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SongShengLin
 * @date 2022/11/18 21:49
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResult {

    private String phone;

    private String identity;
}
