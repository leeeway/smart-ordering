package com.example.order.bean.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OaResultBean<T> implements Serializable {
    public OaResultBean(boolean success, T data, String message, String error) {
        this.success = success;
        this.message = message;
        this.error = error;
        this.data = data;
    }

    public OaResultBean(boolean success, String message, String error) {
        this.success = success;
        this.message = message;
        this.error = error;
    }

    /**
     * 状态信息: 是否成功
     */

    boolean success;

    /**
     * 说明信息
     */
    String message;

    /**
     * 错误编码
     */
    String error;

    /**
     * 状态数据
     */
    T data;

    public static <T> OaResultBean<T> success(T data) {
        return success("", data);
    }

    public static <T> OaResultBean<T> success(String message, T data) {
        return new OaResultBean<T>(true, data,message, "");
    }

    public static <T> OaResultBean<T> paramError(String message, T data) {
        return new OaResultBean<T>(false, data,message, "paramError");
    }

    public static <T> OaResultBean<T> paramError(String message) {
        return paramError(message, null);
    }

    public static <T> OaResultBean<T> statusError(String message, T data) {
        return new OaResultBean<T>(false,data, message, "statusError");
    }

    public static <T> OaResultBean<T> statusError(String message) {
        return new OaResultBean<T>(false,null, message, "statusError");
    }

    public String getErrorMessage() {
        return message;
    }

    public void setErrorMessage(String message) {
        this.message = message;
    }

    public boolean isStatus(){
        return success;
    }

    public boolean getStatus() {
        return success;
    }

    public void setStatus(boolean status) {
        this.success = status;
    }
}

