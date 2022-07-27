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
public interface AirStationHour202207Mapper extends BaseMapper<AirStationHourEntity> {
    void updateBySelective(String tableName,AirStationHourEntity record);

    void createNewTable(@Param(value = "tableName") String tableName);

    void insertSelective(String tableName,AirStationHourEntity record);

    int findTableByName(String tableName);

    AirStationHourEntity selectOneByCond(String tableName, Date dataTime, String stationId);

    void saveBatch(@Param(value = "tableName")String tableName, List<AirStationHourEntity> records);

    void saveOrUpdateBatch(@Param(value = "tableName")String tableName, List<AirStationHourEntity> records);
}
