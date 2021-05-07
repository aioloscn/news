package com.aiolos.news.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章发布类型类型枚举
 * @author Aiolos
 * @date 2020/11/26 7:20 下午
 */
@Getter
@AllArgsConstructor
public enum ArticleAppointType {

    TIMEING(1, "文章定时发布"),
    IMMEDIATELY(0, "文章立即发布");

    private final Integer type;
    private final String value;
}