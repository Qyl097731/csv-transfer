package com.example.csvtransfer.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qyl
 * @program FileUtils.java
 * @Description 文件工具类
 * @createTime 2022-07-25 11:07
 */

@Slf4j
public class FileUtils {
    /**
     * @description 查找路径下的文佳
     * @param path
     * @return List<String>
     * @author qyl
     * @date 2022/7/25 11:07
     */
    public static List<String> findFiles(String path){
        List<String> result = null;
        File file = new File(path);
        if(file.exists() && file.isDirectory()){
            result = new ArrayList<>();
            File[] subFiles = file.listFiles();
            for(File subFile : subFiles){
                result.add(subFile.getName());
            }
        }else{
            log.debug("路径：" + path + " ,不存在或不是文件夹");
        }
        return result;
    }
}
