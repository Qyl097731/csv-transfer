package com.example.csvtransfer;

import com.example.csvtransfer.bean.KeyBean;
import com.example.csvtransfer.config.DataConfig;
import com.example.csvtransfer.entity.AirCity;
import com.example.csvtransfer.entity.AirCityHourEntity;
import com.example.csvtransfer.entity.AirStationHourEntity;
import com.example.csvtransfer.mapper.AirCityMapper;
import com.example.csvtransfer.mapper.AirCityHour202207Mapper;
import com.example.csvtransfer.mapper.AirStationHour202207Mapper;
import com.example.csvtransfer.utils.DateUtil;
import com.example.csvtransfer.utils.FileUtils;
import com.example.csvtransfer.utils.UuidUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class CsvTransferApplicationTests {

    enum TABLE_PREFIX {
        CITY_PREFIX("t_yhdr_cd_air_city_hour"),
        STATION_PREFIX("t_yhdr_cd_air_station_hour");

        private String value;

        TABLE_PREFIX(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 污染物类型，方便程序通过反射获取
     */
    public static final Map<Integer, String> POLLUTION_TYPE = new HashMap<Integer, String>() {{
        put(0, "aqi");
        put(1, "pm25");
        put(2, "pm2524");
        put(3, "pm10");
        put(4, "pm1024");
        put(5, "so2");
        put(6, "so224");
        put(7, "no2");
        put(8, "no224");
        put(9, "o3");
        put(10, "o324");
        put(11, "o38");
        put(12, "o3824");
        put(13, "co");
        put(14, "co24");
    }};

    /**
     * 污染物类型，方便程序通过反射获取
     */
    public static final Integer NTHREAD = Runtime.getRuntime().availableProcessors() * 2 + 1;

    @Autowired
    private DataConfig dataConfig;

    @Autowired
    private AirCityMapper airCityMapper;

    @Autowired
    private AirCityHour202207Mapper airCityHourMapper;

    @Autowired
    private AirStationHour202207Mapper airStationHourMapper;


    @Test
    void contextLoads() {
    }


    @Test
    public void stationDataTrans() {
        Long startTime = System.currentTimeMillis();
        //线程池
        ExecutorService service = Executors.newFixedThreadPool(NTHREAD);
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        // 获取所有的csv文件
        String url = dataConfig.getStation();
        List<String> stationFiles = FileUtils.findFiles(url);

        //遍历文件
        for (String fileName : stationFiles) {
            // table暂存当前文件的所有bean
            Map<KeyBean, AirStationHourEntity> table = new HashMap<>();
            // table中的主键：站点id和时间
            KeyBean keyBean = new KeyBean();
            // 日期
            Date dataTime = null;
            // 文件所对应的数据库名字
            String tableName = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.indexOf(".") - 2);
            tableName = TABLE_PREFIX.STATION_PREFIX.value.concat(tableName);
            // 是否创建数据库
            if (airStationHourMapper.findTableByName(tableName) == 0) {
                airStationHourMapper.createNewTable(tableName);
            }

            try {
                // 读取数据
                String path = url.concat(fileName);
                CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
                // 空行不处理
                List<String[]> lines = reader.readAll();
                if (CollectionUtils.isEmpty(lines)) {
                    continue;
                }

                //把站点存起来 方便后面站点和时间唯一确定数据
                String[] stationIds = lines.get(0);
                List<String> stationList = Arrays.asList(stationIds).subList(3, stationIds.length);

                int totalRow = lines.size();
                //遍历csv每一行
                for (int i = 1; i < totalRow; i++) {
                    // 行数据
                    String[] columns = lines.get(i);

                    // 获取这一行污染物
                    Field field = AirStationHourEntity.class.getDeclaredField(POLLUTION_TYPE.get((i - 1) % 15));

                    // 计算时间
                    if (field.getName().equals("aqi")) {
                        dataTime = DateUtil.localDateTime2Date(DateUtil.parseYMD(columns[0]).plusHours(Long.parseLong(columns[1])));
                    }

                    //遍历列
                    int totalCollum = columns.length;
                    for (int j = 3; j < totalCollum; j++) {
                        // 空数据不管
                        if (Strings.isNullOrEmpty(columns[j])) {
                            continue;
                        }
                        //站点id
                        String stationId = stationList.get(j - 3);
                        // 更新bean
                        field.setAccessible(true);
                        // 根据time 以及 stationId 查临时数据库 ： 更新 or 新增
                        // 维护临时数据库
                        keyBean.setDate(dataTime).setId(stationId);
                        AirStationHourEntity airStationHourEntity = table.get(keyBean);
                        if (null != airStationHourEntity) {
                            if (field.getType().equals(Double.class)) {
                                field.set(airStationHourEntity, Double.parseDouble(columns[j]));
                            } else {
                                field.set(airStationHourEntity, Integer.parseInt(columns[j]));
                            }
                        } else {
                            airStationHourEntity = new AirStationHourEntity();
                            if (field.getType().equals(Double.class)) {
                                field.set(airStationHourEntity, Double.parseDouble(columns[j]));
                            } else {
                                field.set(airStationHourEntity, Integer.parseInt(columns[j]));
                            }
                            airStationHourEntity.setDataId(UuidUtils.get32UUID());
                            airStationHourEntity.setDataTime(dataTime);
                            airStationHourEntity.setStationId(stationId);
                        }
                        table.put(keyBean, airStationHourEntity);
                        field.setAccessible(false);
                    }
                }
            } catch (IOException | CsvException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            // 所有的处理完的数据
            List<AirStationHourEntity> records = new ArrayList<>(table.values());
            int basic = 0, total = records.size();
            //每个线程执行6000条数据批量插入
            do {
                int finalBasic = basic;
                String finalTableName = tableName;
                CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                    List<AirStationHourEntity> subRecords = records.subList(finalBasic * 6000, Math.min((1 + finalBasic) * 6000, total));
                    airStationHourMapper.saveOrUpdateBatch(finalTableName, subRecords);
                }, service);
                futureList.add(completableFuture);
                basic++;
            } while (basic * 6000 < total);
        }

        for (CompletableFuture<Void> future : futureList) {
            future.join();
        }

        Long endTime = System.currentTimeMillis();
        System.out.println("时间为： " + (endTime - startTime) + "ms");
    }

    @Test
    public void cityDataTrans() {
        long startTime = System.currentTimeMillis();
        //线程池
        ExecutorService service = Executors.newFixedThreadPool(NTHREAD);
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        // 获取所有的csv文件
        String url = dataConfig.getCity();
        List<String> cityFiles = FileUtils.findFiles(url);

        // 遍历文件
        for (String fileName : cityFiles) {
            // table暂存当前文件的所有bean
            Map<KeyBean, AirCityHourEntity> table = new HashMap<>();
            // table中的主键：站点id和时间
            KeyBean keyBean = new KeyBean();
            // 日期
            Date dataTime = null;

            // 不同的文件表对应的数据表
            // 表是否存在 不存在就直接创建
            String tableName = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.indexOf(".") - 2);
            tableName = TABLE_PREFIX.CITY_PREFIX.value.concat(tableName);
            if (airCityHourMapper.findTableByName(tableName) == 0) {
                airCityHourMapper.createNewTable(tableName);
            }

            try {
                // 获取csv文件名
                String path = url.concat(fileName);
                CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
                // 空行不处理
                List<String[]> lines = reader.readAll();
                if (CollectionUtils.isEmpty(lines)) {
                    continue;
                }

                //把城市存起来 方便后面站点和时间唯一确定数据
                String[] cityList = lines.get(0);
                Map<String, String> cityTable = new HashMap<>();

                int len = cityList.length;
                for (int k = 3; k < len; k++) {
                    AirCity airCity = airCityMapper.selectByName(cityList[k]);
                    if (airCity != null) {
                        cityTable.put(cityList[k], airCity.getCode());
                    }
                }

                int totalRow = lines.size();
                //遍历行
                for (int i = 1; i < totalRow; i++) {
                    // 行数据
                    String[] columns = lines.get(i);

                    // 获取当前行的污染物
                    Field field = AirCityHourEntity.class.getDeclaredField(POLLUTION_TYPE.get((i - 1) % 15));

                    // 计算时间
                    if (field.getName().equals("aqi")) {
                        dataTime = DateUtil.localDateTime2Date(DateUtil.parseYMD(columns[0]).plusHours(Long.parseLong(columns[1])));
                    }

                    //遍历列
                    int totalCollum = columns.length;
                    for (int j = 3; j < totalCollum; j++) {
                        // 空数据不管
                        if (Strings.isNullOrEmpty(columns[j])) {
                            continue;
                        }

                        String code = cityTable.get(cityList[j]);
                        if (null == code) {
                            continue;
                        }
                        // 更新bean
                        field.setAccessible(true);
                        // 根据time 以及 stationId 查临时数据库 ： 更新 or 新增
                        // 维护临时数据库
                        keyBean.setDate(dataTime).setId(code);
                        AirCityHourEntity airCityHourEntity = table.get(keyBean);
                        if (null != airCityHourEntity) {
                            if (field.getType().equals(Double.class)) {
                                field.set(airCityHourEntity, Double.parseDouble(columns[j]));
                            } else {
                                field.set(airCityHourEntity, Integer.parseInt(columns[j]));
                            }
                        } else {
                            airCityHourEntity = new AirCityHourEntity();
                            if (field.getType().equals(Double.class)) {
                                field.set(airCityHourEntity, Double.parseDouble(columns[j]));
                            } else {
                                field.set(airCityHourEntity, Integer.parseInt(columns[j]));
                            }
                            airCityHourEntity.setDataId(UuidUtils.get32UUID());
                            airCityHourEntity.setDataTime(dataTime);
                            airCityHourEntity.setCCode(code);
                        }
                        table.put(keyBean, airCityHourEntity);
                        field.setAccessible(false);
                    }
                }
            } catch (IOException | CsvException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            // 所有的处理完的数据
            List<AirCityHourEntity> records = new ArrayList<>(table.values());
            int basic = 0, total = records.size();
            //每个线程执行6000条数据批量插入
            do {
                int finalBasic = basic;
                String finalTableName = tableName;
                CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                    List<AirCityHourEntity> subRecords = records.subList(finalBasic * 6000, Math.min((1 + finalBasic) * 6000, total));
                    airCityHourMapper.saveOrUpdateBatch(finalTableName, subRecords);
                }, service);
                futureList.add(completableFuture);
                basic++;
            } while (basic * 6000 < total);

            for (CompletableFuture<Void> future : futureList) {
                future.join();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("时间为 ： " + (endTime - startTime) + "ms");
        }
    }
}