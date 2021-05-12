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

    private List<Category> data;

    private String database;

    private long es;

    private int id;

    private boolean isDdl;

    private MysqlType mysqlType;

    private String old;

    private List<String> pkNames;

    private String sql;

    private SqlType sqlType;

    private String table;

    private long ts;

    private String type;
}
