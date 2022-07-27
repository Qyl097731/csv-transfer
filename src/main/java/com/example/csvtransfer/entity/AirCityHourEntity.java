package com.example.csvtransfer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author qyl
 * @since 2022-07-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AirCityHourEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "data_id", type = IdType.ID_WORKER_STR)
    private String dataId;

    private String cCode;

    private Date dataTime;

    @TableField("AQI")
    private Integer aqi;

    private String priPol;

    @TableField("CO")
    private Double co;

    @TableField("CO_24")
    private Double co24;

    @TableField("NO2")
    private Integer no2;

    @TableField("NO2_24")
    private Integer no224;

    @TableField("SO2")
    private Integer so2;

    @TableField("SO2_24")
    private Integer so224;

    @TableField("O3")
    private Integer o3;

    @TableField("O3_24")
    private Integer o324;

    @TableField("O3_8")
    private Integer o38;

    @TableField("O3_8_24")
    private Integer o3824;

    @TableField("PM10")
    private Integer pm10;

    @TableField("PM10_24")
    private Integer pm1024;

    @TableField("PM2_5")
    private Integer pm25;

    @TableField("PM2_5_24")
    private Integer pm2524;

}
