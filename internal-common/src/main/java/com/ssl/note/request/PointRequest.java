package com.ssl.note.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/04 16:11
 * @Describe:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRequest {

    private String tid;

    private String trid;

    private PointDTO[] points;
}
