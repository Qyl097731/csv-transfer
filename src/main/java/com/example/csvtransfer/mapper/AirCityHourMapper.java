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
public interface AirCityHourMapper extends BaseMapper<AirCityHourEntity> {
    /**
     * 查询城市
     * @param tableName
     * @param dataTime
     * @param code
     * @return AirCityHourEntity
     */
    AirCityHourEntity selectOneByCond(String tableName, Date dataTime, String code);

    /**
     * 单条数据更新
     * @param tableName
     * @param record
     */
    void updateBySelective(String tableName, AirCityHourEntity record);

    /**
     * 单条数据插入
     * @param tableName
     * @param record
     */
    void insertSelective(String tableName, AirCityHourEntity record);

    /**
     * 创建表
     * @param tableName
     */
    void createNewTable(@Param("tableName")String tableName);

    /**
     * 查找表
     * @param tableName
     * @return int
     */
    int findTableByName(@Param("tableName")String tableName);

    /**
     * 批量插入或者更新
     * @param tableName
     * @param records
     */
    void saveOrUpdateBatch(@Param("tableName") String tableName, List<AirCityHourEntity> records);
}
