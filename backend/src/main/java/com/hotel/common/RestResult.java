package com.hotel.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 */
@Data
public class RestResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    public RestResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public RestResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> RestResult<T> success(T data) {
        return new RestResult<>(ResultCodeConstant.SUCCESS, "操作成功", data);
    }

    public static <T> RestResult<T> success() {
        return new RestResult<>(ResultCodeConstant.SUCCESS, "操作成功", null);
    }

    public static <T> RestResult<T> success(String message, T data) {
        return new RestResult<>(ResultCodeConstant.SUCCESS, message, data);
    }

    public static <T> RestResult<T> error(String message) {
        return new RestResult<>(ResultCodeConstant.ERROR, message, null);
    }

    public static <T> RestResult<T> error(Integer code, String message) {
        return new RestResult<>(code, message, null);
    }

    public static <T> RestResult<T> error(ResultCodeConstant resultCode) {
        return new RestResult<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> RestResult<T> error(ResultCodeConstant resultCode, String message) {
        return new RestResult<>(resultCode.getCode(), message, null);
    }
}
