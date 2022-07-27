package com.example.csvtransfer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author qyl
 * @program DataConfig.java
 * @Description 源数据目录配置类
 * @createTime 2022-07-25 10:57
 */
@ConfigurationProperties(prefix = "data-package")
@Component
@Data
public class DataConfig {
    /**
     * 站点文件夹路径
     */
    private String station;

    /**
     * 城市文件夹路径
     */
    private String city;
}
