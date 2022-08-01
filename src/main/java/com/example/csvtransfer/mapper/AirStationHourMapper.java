package com.example.csvtransfer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.csvtransfer.entity.AirStationHourEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author qyl
 * @since 2022-07-25
 */
@Mapper
public interface AirStationHourMapper extends BaseMapper<AirStationHourEntity> {
    /**
     * 单数据更新
     * @param tableName
     * @param record
     */
    void updateBySelective(String tableName,AirStationHourEntity record);

    /**
     * 创建表
     * @param tableName
     */
    void createNewTable(@Param(value = "tableName") String tableName);

    /**
     * 单数据插入
     * @param tableName
     * @param record
     */
    void insertSelective(String tableName,AirStationHourEntity record);

    /**
     * 查找表是否存在
     * @param tableName
     * @return int
     */
    int findTableByName(String tableName);

    /**
     * 查询某数据是否存在
     * @param tableName
     * @param dataTime
     * @param stationId
     * @return AirStationHourEntity
     */
    AirStationHourEntity selectOneByCond(String tableName, Date dataTime, String stationId);

    /**
     * 批量插入或者更新
     * @param tableName
     * @param records
     */
    void saveOrUpdateBatch(@Param(value = "tableName")String tableName, List<AirStationHourEntity> records);
}
