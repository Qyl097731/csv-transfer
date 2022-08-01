package com.example.csvtransfer.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author qyl
 * @program AirHourBean.java
 * @Description 暂存csv数据对象的bean
 * @createTime 2022-07-28 12:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AirHourBean {
    private String dataId;

    private String cCode;

    private String stationId;

    private Date dataTime;

    private Integer aqi;

    private String priPol;

    private Double co;

    private Double co24;

    private Integer no2;

    private Integer no224;

    private Integer so2;

    private Integer so224;

    private Integer o3;

    private Integer o324;

    private Integer o38;

    private Integer o3824;

    private Integer pm10;

    private Integer pm1024;

    private Integer pm25;

    private Integer pm2524;
}
