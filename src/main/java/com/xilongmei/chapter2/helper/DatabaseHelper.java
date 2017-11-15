package com.xilongmei.chapter2.helper;

import com.xilongmei.chapter2.service.CustomerService;
import com.xilongmei.chapter2.util.PropsUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final QueryRunner QUERY_RUNNER=new QueryRunner();

    static {
        Properties conf = PropsUtil.LoadProps("config.properties");
        DRIVER = conf.getProperty("jdbc.driver");
        URL = conf.getProperty("jdbc.url");
        USERNAME = conf.getProperty("jdbc.username");
        PASSWORD = conf.getProperty("jdbc.password");

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.error("config 文件未找到", e);

        }


    }

    /***
     * 获取数据库连接
     * */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            LOGGER.error(" 获取数据库连接失败！", e);
        }
        return conn;
    }

    public  static void closeConnection(Connection conn){
        if (conn!=null){
            try {
                conn.close();
            }
            catch (SQLException e){
                LOGGER.error("关闭数据库连接失败！",e);
            }

        }

    }
}
