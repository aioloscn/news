package com.aiolos.news.utils;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 目前CRUD使用mybatis-plus的BaseMapper，如果使用tk.mybatis则将mybatis-generator.xml中
 * <property name="mappers" value="com.baomidou.mybatisplus.core.mapper.BaseMapper"/>
 * 的value改成这个路径
 * @author Aiolos
 * @date 2020/9/24 5:12 下午
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
