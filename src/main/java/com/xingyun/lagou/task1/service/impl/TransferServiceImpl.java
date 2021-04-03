package com.xingyun.lagou.task1.service.impl;

import com.xingyun.lagou.task1.annotation.MyAutowired;
import com.xingyun.lagou.task1.annotation.MyService;
import com.xingyun.lagou.task1.annotation.MyTransactional;
import com.xingyun.lagou.task1.dao.IAccountDao;
import com.xingyun.lagou.task1.dto.Account;
import com.xingyun.lagou.task1.dto.TransferParam;
import com.xingyun.lagou.task1.service.ITransferService;

/**
 * @author xingyun
 * @date 2021/3/30
 */
@MyService("transferService")
public class TransferServiceImpl implements ITransferService {

    @MyAutowired
    private IAccountDao accountDao;

    public void setAccountDao(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    @MyTransactional
    public void transfer(TransferParam transferParam) {


        Account from = accountDao.getAccountByCardNo(transferParam.getFromCardNo());
        Account to = accountDao.getAccountByCardNo(transferParam.getToCardNo());

        from.setAmount(from.getAmount().subtract(transferParam.getAmount()));
        to.setAmount(to.getAmount().add(transferParam.getAmount()));


        accountDao.updateAccountByCardNo(from);
        int i = 1/0;
        accountDao.updateAccountByCardNo(to);


    }
}
