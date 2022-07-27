package com.example.csvtransfer.utils;

import java.util.UUID;

/**
 * @author qyl
 * @program UuidUtils.java
 * @Description id生成
 * @createTime 2022-07-25 17:05
 */
public class UuidUtils {
    public static String get32UUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }
}
