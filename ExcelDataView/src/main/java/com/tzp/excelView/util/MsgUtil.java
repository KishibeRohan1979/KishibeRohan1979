package com.tzp.excelView.util;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Dong
 */
@Data
public class MsgUtil<T> implements Serializable {

    /**
     * 用于向前端回复消息的类
     */
    private Boolean flag;

    private Integer code;

    private String message;

    private T data;

    public MsgUtil(Boolean flag, Integer code, String message, T data) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public MsgUtil() {

    }

    public static <T> MsgUtil<T> success() {
        return new MsgUtil<>(true, 0, "操作成功", null);
    }

    public static <T> MsgUtil<T> success(String message) {
        return new MsgUtil<>(true, 0, message, null);
    }

    public static <T> MsgUtil<T> success(String message, T data) {
        return new MsgUtil<>(true, 0, message, data);
    }

    public static <T> MsgUtil<T> fail() {
        return new MsgUtil<>(false, -1, "操作失败", null);
    }

    public static <T> MsgUtil<T> fail(String message) {
        return new MsgUtil<>(false, -1, message, null);
    }

    public static <T> MsgUtil<T> fail(Integer code, String message) {
        return new MsgUtil<>(false, code, message, null);
    }

    public static <T> MsgUtil<T> fail(String message, T data) {
        return new MsgUtil<>(false, -1, message, data);
    }

}
