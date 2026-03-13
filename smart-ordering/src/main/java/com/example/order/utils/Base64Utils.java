package com.example.order.utils;

import java.util.Base64;

/**
 * 转码工具类
 * @author leeway
 * @date 2023/8/14/014 16:37
 */
public class Base64Utils {

    public static String encodeToBase64(String input) {
        byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
        return new String(encodedBytes);
    }

    public static String decodeFromBase64(String encodedInput) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedInput.getBytes());
        return new String(decodedBytes);
    }

}
