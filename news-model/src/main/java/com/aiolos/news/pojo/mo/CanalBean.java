package com.aiolos.news.pojo.mo;

import com.aiolos.news.pojo.Category;
import lombok.Data;

import java.util.List;

/**
 * @author Aiolos
 * @date 2021/5/11 11:48 下午
 */
@Data
public class CanalBean {

    /**
     * 数据
     */
    private List<Category> data;

    private String database;

    private long es;

    private int id;

    private boolean isDdl;

    /**
     * 表结构的字段类型
     */
    private MysqlType mysqlType;

    /**
     * UPDATE语句，旧数据
     */
    private String old;

    /**
     * 主键名称
     */
    private List<String> pkNames;

    /**
     * sql语句
     */
    private String sql;

    private SqlType sqlType;

    private String table;

    private long ts;

    private String type;
}
