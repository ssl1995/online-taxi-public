package com.ssl.note.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
public class PassengerUser implements Serializable {

    private static final long serialVersionUID = -3338383135459734730L;
    private Long id;

    private String passengerName;

    private String passengerPhone;

    /**
     * 用户性别：0=未知，1=男，2=女
     */
    private Byte passengerGender;

    /**
     * 用户头像
     */
    private String profilePhoto;

    /**
     * 数据是否删除：0=有效，1=无效
     */
    private Byte status;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
