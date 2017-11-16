package com.xilongmei.chapter2.helper;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import com.xilongmei.chapter2.service.CustomerService;
import com.xilongmei.chapter2.util.CollectionUtil;
import com.xilongmei.chapter2.util.PropsUtil;
import org.apache.commons.dbcp2.BasicDataSource;
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
import java.util.*;

public final class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    private static final ThreadLocal<Connection> COLLECTION_HOLDER = new ThreadLocal<Connection>();
    private static final BasicDataSource DATA_SOURCE;

    static {
        Properties conf = PropsUtil.LoadProps("config.properties");
        String DRIVER = conf.getProperty("jdbc.driver");
        String URL = conf.getProperty("jdbc.url");
        String USERNAME = conf.getProperty("jdbc.username");
        String PASSWORD = conf.getProperty("jdbc.password");
        DATA_SOURCE=new BasicDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);
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
                conn = DATA_SOURCE.getConnection();
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


    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> feildMap) {
        if (CollectionUtil.isEmpty(feildMap)) {
            LOGGER.error("can not insert entity: fieldMap is empty");
            return false;
        }
        String sql = "INSERT INTO " + getTableName(entityClass) + "";
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : feildMap.keySet()) {
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += columns + " VALUES " + values;
        Object[] params = feildMap.values().toArray();
        return executeUpdate(sql, params) == 1;

    }

    public static <T> boolean updateEntity(Class<T> entity, long id, Map<String, Object> feildMap) {
        if (CollectionUtil.isEmpty(feildMap)) {
            LOGGER.error("can not insert entity: fieldMap is empty");
            return false;
        }
        String sql = "UPDATE " + getTableName(entity) + " SET";
        StringBuilder columns = new StringBuilder();
        for (String fieldName : feildMap.keySet()) {
            columns.append(fieldName).append("=?, ");

        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id=?";
        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(feildMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();
        return executeUpdate(sql, params) == 1;
    }


    private static int executeUpdate(String sql, Object... params) {
        int rows = 0;
        Connection conn = getConnection();
        try {
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("execute Update failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return rows;
    }

    public static <T> boolean deleteEntity(Class<T> entityClass,long id){
        String sql="DELETE FROM "+getTableName(entityClass)+" WHERE id=?";
        return executeUpdate(sql,id)==1;

    }
    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }


}
