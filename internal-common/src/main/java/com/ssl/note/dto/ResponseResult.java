package com.ssl.note.dto;

import com.ssl.note.constant.CommonStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author SongShengLin
 * @date 2022/11/16 23:24
 * @description
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ResponseResult<T> {

    private Integer code;
    private String message;
    private T data;

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<T>()
                .setCode(CommonStatusEnum.SUCCESS.getCode())
                .setMessage(CommonStatusEnum.SUCCESS.getMessage())
                .setData(data);
    }

    public static <T> ResponseResult<T> fail( T data) {
        return new ResponseResult<T>()
                .setCode(CommonStatusEnum.FAIL.getCode())
                .setMessage(CommonStatusEnum.FAIL.getMessage())
                .setData(data);
    }

    public static <T> ResponseResult<T> fail(Integer code, String message, T data) {
        return new ResponseResult<T>()
                .setCode(code)
                .setMessage(message)
                .setData(data);
    }
}
