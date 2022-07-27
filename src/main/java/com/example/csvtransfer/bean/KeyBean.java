package com.example.csvtransfer.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author qyl
 * @program KeyBean.java
 * @Description 作为临时table的键值对
 * @createTime 2022-07-26 09:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class KeyBean {
    /**
     * cCode / stationId
     */
    private String id;

    /**
     * 日期
     */
    private Date date;
}
