package com.xingyun.lagou.task1.utils;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author xingyun
 * @date 2021/3/30
 */
public class DataSourceUtils {

    private static DataSource dataSource ;

    static {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("jdbc:mysql://47.98.59.188:3306/work?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false");
        druidDataSource.setUsername("lagou");
        druidDataSource.setPassword("lagou");

        dataSource = druidDataSource;
    }

    public static DataSource getDataSource(){
        return dataSource;
    }

}
