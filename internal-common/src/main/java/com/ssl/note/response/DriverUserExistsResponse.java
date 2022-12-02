package com.ssl.note.response;

import lombok.Data;

/**
 * @Author: SongShengLin
 * @Date: 2022/12/02 21:06
 * @Describe:
 */
@Data
public class DriverUserExistsResponse {

    private String driverPhone;

    private Integer ifExists;

}
