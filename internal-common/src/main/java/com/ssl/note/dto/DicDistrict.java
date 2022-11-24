package com.ssl.note.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/24 21:13
 * @Describe:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DicDistrict {


    private String addressCode;


    private String addressName;

    private String parentAddressCode;

    private Integer level;

}
