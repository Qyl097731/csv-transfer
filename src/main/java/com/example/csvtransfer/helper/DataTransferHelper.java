package com.example.csvtransfer.helper;

import com.example.csvtransfer.bean.AirHourBean;
import com.example.csvtransfer.bean.KeyBean;
import com.example.csvtransfer.config.DataConfig;
import com.example.csvtransfer.constant.DataTransferConst;
import com.example.csvtransfer.entity.AirCity;
import com.example.csvtransfer.entity.AirStationHourEntity;
import com.example.csvtransfer.mapper.AirCityMapper;
import com.example.csvtransfer.service.DataTransferService;
import com.example.csvtransfer.utils.DateUtil;
import com.example.csvtransfer.utils.FileUtils;
import com.example.csvtransfer.utils.UuidUtils;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author qyl
 * @program DataTransferHelper.java
 * @Description 数据传输杂项
 * @createTime 2022-07-28 11:56
 */
@Component
public class DataTransferHelper {
    public static final String STATION = "station";

    @Autowired
    private AirCityMapper airCityMapper;

    /**
     * @param path
     * @return List<AirHourBean>
     * @description 读取csv 解析csv数据
     * @author qyl
     * @date 2022/7/28 13:26
     */
    public List<AirHourBean> getCsvData(String path) {
        // table暂存当前文件的所有bean
        Map<KeyBean, AirHourBean> table = new HashMap<>();

        String type;

        try {
            // 读取数据
            CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            List<String[]> lines = reader.readAll();

            if (CollectionUtils.isEmpty(lines)) {
                return null;
            }

            if (path.contains(STATION)) {
                type = "station";
            } else {
                type = "city";
            }
            dataEncapsulate(lines, table, type);
        } catch (IOException | CsvException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(table.values());
    }

    /**
     * @param lines
     * @param table
     * @param type
     * @return void
     * @description 将csv数据封装成bean 封装在 临时table中
     * @author qyl
     * @date 2022/7/28 14:34
     */
    public void dataEncapsulate(List<String[]> lines, Map<KeyBean, AirHourBean> table, String type) throws NoSuchFieldException, IllegalAccessException {
        // table中的主键：站点id和时间
        KeyBean keyBean = new KeyBean();
        // 日期
        Date dataTime = new Date();
        //站点 方便后面站点和时间唯一确定数据
        List<String> stationList = new ArrayList<>();
        //城市 方便后面城市和时间唯一确定数据
        String[] cityList = null;
        Map<String, String> cityTable = null;

        String stationId = null;
        String cCode = null;

        if (STATION.equals(type)) {
            //把站点存起来 方便后面站点和时间唯一确定数据
            String[] stationIds = lines.get(0);
            stationList = Arrays.asList(stationIds).subList(3, stationIds.length);
        } else {
            //把城市存起来 方便后面城市和时间唯一确定数据
            cityList = lines.get(0);
            cityTable = new HashMap<>();
            int len = cityList.length;
            for (int k = 3; k < len; k++) {
                AirCity airCity = airCityMapper.selectByName(cityList[k]);
                if (airCity != null) {
                    cityTable.put(cityList[k], airCity.getCode());
                }
            }
        }

        int totalRow = lines.size();
        //遍历csv每一行
        for (int i = 1; i < totalRow; i++) {
            // 行数据
            String[] columns = lines.get(i);
            // 获取这一行污染物
            Field field = AirHourBean.class.getDeclaredField(DataTransferConst.POLLUTION_TYPE.get((i - 1) % 15));
            // 计算时间
            if ("aqi".equals(field.getName())) {
                dataTime = DateUtil.localDateTime2Date(DateUtil.parseYMD(columns[0]).plusHours(Long.parseLong(columns[1])));
            }

            //遍历列
            int totalCollum = columns.length;
            for (int j = 3; j < totalCollum; j++) {
                // 空数据不管
                if (Strings.isNullOrEmpty(columns[j])) {
                    continue;
                }
                if ("station".equals(type)) {
                    //站点id
                    stationId = stationList.get(j - 3);
                    keyBean.setDate(dataTime).setId(stationId);
                } else {
                    cCode = cityTable.get(cityList[j]);
                    if (null == cCode) {
                        continue;
                    }
                    keyBean.setDate(dataTime).setId(cCode);
                }

                field.setAccessible(true);
                // 维护临时数据库
                AirHourBean airHourBean = table.get(keyBean);
                if (null != airHourBean) {
                    if (field.getType().equals(Double.class)) {
                        field.set(airHourBean, Double.parseDouble(columns[j]));
                    } else {
                        field.set(airHourBean, Integer.parseInt(columns[j]));
                    }
                } else {
                    airHourBean = new AirHourBean();
                    if (field.getType().equals(Double.class)) {
                        field.set(airHourBean, Double.parseDouble(columns[j]));
                    } else {
                        field.set(airHourBean, Integer.parseInt(columns[j]));
                    }
                    airHourBean.setDataId(UuidUtils.get32UUID());
                    airHourBean.setDataTime(dataTime);
                    if ("station".equals(type)) {
                        airHourBean.setStationId(stationId);
                    } else {
                        airHourBean.setCCode(cCode);
                    }
                }
                table.put(keyBean, airHourBean);
                field.setAccessible(false);
            }
        }
    }

    /**
     * 获取指定文件夹内的文件
     * @param url
     * @return List<String>
     */
    public List<String> getCsvFiles(String url) {
        return FileUtils.findFiles(url);
    }


    /**
     * @description 获取线程池
     * @param
     * @return ExecutorService
     * @author qyl
     * @date 2022/7/28 14:42
     */
    public ExecutorService getThreadPool() {
        return new ThreadPoolExecutor(DataTransferConst.N_CPU, 2 * DataTransferConst.N_CPU + 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build());
    }
}
