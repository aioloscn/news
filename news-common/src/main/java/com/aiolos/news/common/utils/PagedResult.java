package com.aiolos.news.common.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/24 1:55 上午
 */
@Data
public class PagedResult {

    /**
     * 当前页数
     */
    private long current;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 每行显示内容
     */
    private List<?> records;
}
