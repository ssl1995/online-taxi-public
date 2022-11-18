package com.ssl.note.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SongShengLin
 * @date 2022/11/14 22:20
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCodeDTO {

    private String passengerPhone;

    private String verificationCode;

}
