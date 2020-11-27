package com.aiolos.news.common.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Aiolos
 * @date 2020/11/24 1:55 上午
 */
@Getter
@Setter
@ToString
public class        PagedResult {

    private long current;       // 当前页数
    private long pages  ;      // 总页数
    private long total;   // 总记录数
    private List<?> records;   // 每行显示内容
}
