package com.xingyun.lagou.task1.dao;

import com.xingyun.lagou.task1.dto.Account;

/**
 * @author xingyun
 * @date 2021/3/30
 */
public interface IAccountDao {

    Account getAccountByCardNo(String cardNo);

    int updateAccountByCardNo(Account account);
}
