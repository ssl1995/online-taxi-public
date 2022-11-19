package com.ssl.note.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: SongShengLin
 * @Date: 2022/11/18 08:42
 * @Describe:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerUser {

    private Long id;

    private String passengerName;

    private String passengerPhone;

    /**
     * 用户性别：0=女，1=男
     */
    private Byte passengerGender;

    /**
     * 数据是否删除：0=有效，1=无效
     */
    private Byte status;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    /**
     * 用户头像
     */
    private String profilePhoto;
}
