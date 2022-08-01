package com.example.csvtransfer.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qyl
 * @program DataTransferConst.java
 * @Description 数据处理时所需要的常量类
 * @createTime 2022-07-28 11:18
 */
public class DataTransferConst {

    /**
     * 表前缀
     */
    public enum TableType {
        /**
         * 城市、站点
         */
        CITY,STATION
    }

    /**
     * 城市表前缀
     */
    public static final String CITY_TABLE_PREFIX = "t_yhdr_cd_air_city_hour";

    /**
     * 站点表前缀
     */
    public static final String STATION_TABLE_PREFIX = "t_yhdr_cd_air_station_hour";


    /**
     * 污染物类型，方便程序通过反射获取
     */
    public static final Map<Integer, String> POLLUTION_TYPE = new HashMap<Integer, String>() {{
        put(0, "aqi");
        put(1, "pm25");
        put(2, "pm2524");
        put(3, "pm10");
        put(4, "pm1024");
        put(5, "so2");
        put(6, "so224");
        put(7, "no2");
        put(8, "no224");
        put(9, "o3");
        put(10, "o324");
        put(11, "o38");
        put(12, "o3824");
        put(13, "co");
        put(14, "co24");
    }};

    /**
     * 污染物类型，方便程序通过反射获取
     */
    public static final Integer N_CPU = Runtime.getRuntime().availableProcessors();

}
