package com.xingyun.lagou.task1.service;

import com.xingyun.lagou.task1.dto.TransferParam;

/**
 * @author xingyun
 * @date 2021/3/30
 */
public interface ITransferService {

    /**
     * 转账
     * @param transferParam
     */
    void transfer(TransferParam transferParam);
}
