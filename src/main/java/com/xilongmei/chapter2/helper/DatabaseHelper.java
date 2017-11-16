package com.xilongmei.chapter2.helper;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.xilongmei.chapter2.service.CustomerService;
import com.xilongmei.chapter2.util.CollectionUtil;
import com.xilongmei.chapter2.util.PropsUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.omg.CORBA.OBJ_ADAPTER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    private static final ThreadLocal<Connection> COLLECTION_HOLDER = new ThreadLocal<Connection>();

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
        Connection conn = COLLECTION_HOLDER.get();

        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                LOGGER.error(" 获取数据库连接失败！", e);
            } finally {
                COLLECTION_HOLDER.set(conn);
            }

        }


        return conn;
    }

    public static void closeConnection() {
        Connection conn = COLLECTION_HOLDER.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("关闭数据库连接失败！", e);
            } finally {
                COLLECTION_HOLDER.remove();
            }

        }

    }


    /**
     * 查询实体类数据集
     **/
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> entityList;
        Connection conn = getConnection();
        try {
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);

        } finally {
            closeConnection();
        }
        return entityList;

    }

    /**
     * 查询实体类
     **/
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        T entity;
        Connection conn = getConnection();
        try {
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entity;
    }

    /***
     * 执行查询语句
     *
     * **/
    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> result;
        Connection conn = getConnection();
        try {
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("execute query failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return result;
    }


    public  static <T> boolean insertEntity(Class<T> entityClass, Map<String,Object> feildMap){
        if(CollectionUtil.isEmpty(feildMap)){
           LOGGER.error("can not insert entity: fieldMap is empty");
           return false;
        }
        String sql="INSERT INTO "+getTableName(entityClass)+"";


    }

    private static  String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }


}
