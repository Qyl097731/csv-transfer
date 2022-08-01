package com.example.csvtransfer;


import com.example.csvtransfer.constant.DataTransferConst;
import com.example.csvtransfer.service.DataTransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CsvTransferApplicationTests {
    @Autowired
    private DataTransferService dataTransferService;

    @Test
    void contextLoads() {
    }


    @Test
    public void dataTransferTest(){
//        dataTransferService.dataTransfer(DataTransferConst.TableType.CITY);
        dataTransferService.dataTransfer(DataTransferConst.TableType.STATION);
    }
}