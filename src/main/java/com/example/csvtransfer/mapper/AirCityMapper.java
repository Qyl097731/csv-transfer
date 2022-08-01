package com.example.csvtransfer.mapper;

import com.example.csvtransfer.entity.AirCity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qyl
 * @since 2022-07-25
 */


@Mapper
public interface AirCityMapper extends BaseMapper<AirCity> {
    /**
     * 模糊查询城市code
     * @param cityName
     * @return AirCity
     */
    @Select("select code, name from t_yhdr_bc_air_city where name like concat(#{cityName,jdbcType=VARCHAR},'_')")
    AirCity selectByName(String cityName);
}
