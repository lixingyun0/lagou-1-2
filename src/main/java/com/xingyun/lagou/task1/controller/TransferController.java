package com.xingyun.lagou.task1.controller;

import com.xingyun.lagou.task1.dto.TransferParam;
import com.xingyun.lagou.task1.service.ITransferService;
import com.xingyun.lagou.task1.utils.BeanFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xingyun
 * @date 2021/3/30
 */
@RestController
@RequestMapping("transfer")
public class TransferController {


    private ITransferService transferService = BeanFactory.getBeanById("transferService");

    @PostMapping("amount")
    public String transfer(@RequestBody TransferParam transferParam){

        transferService.transfer(transferParam);
        return "success";

    }



}
