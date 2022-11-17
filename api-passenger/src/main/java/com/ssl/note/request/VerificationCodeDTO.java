package com.ssl.note.request;

import lombok.Data;

/**
 * @author SongShengLin
 * @date 2022/11/14 22:20
 * @description
 */
@Data
public class VerificationCodeDTO {

    private String passengerPhone;

    private String verificationCode;

}
