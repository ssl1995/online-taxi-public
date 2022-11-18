package com.ssl.note.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/18 08:42
 * @Describe:
 */
@Data
public class PassengerUser {

    private Long id;

    private String passengerName;

    private String passengerPhone;

    private Byte passengerGender;

    private Byte status;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
