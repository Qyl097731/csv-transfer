package com.example.csvtransfer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.csvtransfer.entity.AirCityHourEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qyl
 * @since 2022-07-25
 */
@Mapper
public interface AirCityHour202207Mapper extends BaseMapper<AirCityHourEntity> {

    AirCityHourEntity selectOneByCond(String tableName, Date dataTime, String code);

    void updateBySelective(String tableName, AirCityHourEntity record);

    void insertSelective(String tableName, AirCityHourEntity record);

    void saveBatch(@Param("tableName") String tableName, List<AirCityHourEntity> records);

    void createNewTable(@Param("tableName")String tableName);

    int findTableByName(@Param("tableName")String tableName);

    void saveOrUpdateBatch(@Param("tableName") String tableName, List<AirCityHourEntity> records);
}
