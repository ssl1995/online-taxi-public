package com.ssl.note.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 16:10
 * @Describe:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDTO {

    private String location;
    private String locatetime;

}
