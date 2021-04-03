package com.xingyun.lagou.task1.dao.impl;

import com.xingyun.lagou.task1.annotation.MyService;
import com.xingyun.lagou.task1.dao.IAccountDao;
import com.xingyun.lagou.task1.dto.Account;
import com.xingyun.lagou.task1.utils.ConnectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xingyun
 * @date 2021/3/30
 */
@MyService
public class AccountDaoImpl implements IAccountDao {

    @Override
    public Account getAccountByCardNo(String cardNo) {
        String sql = "select * from account where card_no = ?";
        List<Account> accountList = new ArrayList<>();
        try {
            Connection connection = ConnectionUtils.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,cardNo);
            ResultSet resultSet = preparedStatement.executeQuery();


            while (resultSet.next()) {
                Account account = new Account();
                account.setName(resultSet.getString("name"));
                account.setAmount(resultSet.getBigDecimal("amount"));
                account.setCardNo(resultSet.getString("card_no"));
                accountList.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accountList.get(0);
    }

    @Override
    public int updateAccountByCardNo(Account account) {

        int i = -1;
        String sql = "update account set amount = ?  where card_no = ?";
        Connection connection = null;
        try {
            connection = ConnectionUtils.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setBigDecimal(1,account.getAmount());
            preparedStatement.setString(2,account.getCardNo());
            i = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return i;
    }
}
