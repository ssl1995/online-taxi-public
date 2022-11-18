package com.ssl.note.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/18 08:42
 * @Describe:
 */
@Data
@Builder
public class PassengerUser {

    private Long id;

    private String passengerName;

    private String passengerPhone;

    /**
     * 0=女，1=男
     */
    private Byte passengerGender;

    /**
     * 0=有效，1=无效
     */
    private Byte status;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
