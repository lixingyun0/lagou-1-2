package com.xingyun.lagou.task1;

import com.xingyun.lagou.task1.dto.TransferParam;
import com.xingyun.lagou.task1.service.ITransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class Task1ApplicationTests {

    @Autowired
    private ITransferService transferService;

    @Test
    void contextLoads() {
        System.out.println(transferService);

        TransferParam transferParam = new TransferParam();
        transferParam.setFromCardNo("mm1");
        transferParam.setToCardNo("hh1");
        transferParam.setAmount(new BigDecimal("100.00"));
        transferService.transfer(transferParam);
    }

}
