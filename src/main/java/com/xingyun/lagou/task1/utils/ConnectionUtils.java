package com.xingyun.lagou.task1.utils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author xingyun
 * @date 2021/3/30
 */
public class ConnectionUtils {

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public static Connection getConnection(){
        Connection connection = threadLocal.get();
        if (connection != null){
            return connection;
        }
        try {
            connection = DataSourceUtils.getDataSource().getConnection();
            threadLocal.set(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return threadLocal.get();
    }
}
