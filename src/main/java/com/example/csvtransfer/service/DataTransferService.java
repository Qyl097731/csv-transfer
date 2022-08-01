package com.example.csvtransfer.service;

import com.example.csvtransfer.bean.AirHourBean;
import com.example.csvtransfer.config.DataConfig;
import com.example.csvtransfer.constant.DataTransferConst;
import com.example.csvtransfer.entity.AirCityHourEntity;
import com.example.csvtransfer.entity.AirStationHourEntity;
import com.example.csvtransfer.helper.DataTransferHelper;
import com.example.csvtransfer.mapper.AirCityHourMapper;
import com.example.csvtransfer.mapper.AirCityMapper;
import com.example.csvtransfer.mapper.AirStationHourMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * @author qyl
 * @program AirStationHourService.java
 * @Description airstation
 * @createTime 2022-07-28 10:21
 */
@Service
public class DataTransferService {

    @Autowired
    private DataTransferHelper dataTransferHelper;

    @Autowired
    private DataConfig dataConfig;

    @Autowired
    private AirCityHourMapper airCityHourMapper;

    @Autowired
    private AirStationHourMapper airStationHourMapper;

    /**
     * @description 根据传入的type来获取决定是存取station还是city
     * @param type
     * @return void
     * @author qyl
     * @date 2022/7/28 13:27
     */
    public void dataTransfer(DataTransferConst.TableType type) {
        // 任务列表
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        //获取线程池
        ExecutorService service = dataTransferHelper.getThreadPool();
        // 文件夹路径
        String csvFolderUrl = type == DataTransferConst.TableType.STATION ? dataConfig.getStation() : dataConfig.getCity();
        // 表前缀
        String tablePrefix = type == DataTransferConst.TableType.STATION ? DataTransferConst.STATION_TABLE_PREFIX : DataTransferConst.CITY_TABLE_PREFIX;

        // 获取所有的csv文件
        List<String> csvFiles = dataTransferHelper.getCsvFiles(csvFolderUrl);
        //遍历文件
        for (String fileName : csvFiles) {
            // 文件所对应的数据库名字
            String tableName = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.indexOf(".") - 2);
            tableName = tablePrefix.concat(tableName);
            // 是否创建数据库
            if(type == DataTransferConst.TableType.STATION) {
                if (airStationHourMapper.findTableByName(tableName) == 0) {
                    airStationHourMapper.createNewTable(tableName);
                }
            }else{
                if (airCityHourMapper.findTableByName(tableName) == 0) {
                    airCityHourMapper.createNewTable(tableName);
                }
            }
            // 获取数据
            List<AirHourBean> csvData = dataTransferHelper.getCsvData(csvFolderUrl.concat(fileName));
            if (CollectionUtils.isEmpty(csvData)) {
                continue;
            }
            // 所有的处理完的数据
            int basic = 0, total = csvData.size();
            //每个线程执行6000条数据批量插入
            do {
                int finalBasic = basic;
                String finalTableName = tableName;
                CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                    // 数据转换
                    if(type == DataTransferConst.TableType.STATION){
                        List<AirStationHourEntity> records = csvData.subList(finalBasic * 6000, Math.min((1 + finalBasic) * 6000, total))
                                .stream().map(data -> {
                                    AirStationHourEntity record = new AirStationHourEntity();
                                    BeanUtils.copyProperties(data,record);
                                    return record;
                                }).collect(Collectors.toList());
                        airStationHourMapper.saveOrUpdateBatch(finalTableName, records);
                    }else{
                        List<AirCityHourEntity> records = csvData.subList(finalBasic * 6000, Math.min((1 + finalBasic) * 6000, total))
                                .stream().map(data -> {
                                    AirCityHourEntity record = new AirCityHourEntity();
                                    BeanUtils.copyProperties(data, record);
                                    return record;
                                }).collect(Collectors.toList());
                        airCityHourMapper.saveOrUpdateBatch(finalTableName, records);
                    }
                }, service);
                futureList.add(completableFuture);
                basic++;
            } while (basic * 6000 < total);
        }
        for (CompletableFuture<Void> future : futureList) {
            future.join();
        }
    }

}
