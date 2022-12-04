package com.ssl.note.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 16:51
 * @Describe:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiDriverPointRequest {
    private Long carId;
    private PointDTO[] points;

}
