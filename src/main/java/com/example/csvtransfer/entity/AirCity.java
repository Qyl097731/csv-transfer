package com.example.csvtransfer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class AirCity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "c_id", type = IdType.ID_WORKER_STR)
    private String cId;

    private String code;

    private String name;

    private String enName;

    private String abbre;

    private String pId;

    /**
     * 经度
     */
    private String lon;

    /**
     * 纬度
     */
    private String lat;


}
