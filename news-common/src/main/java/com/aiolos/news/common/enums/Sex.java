package com.aiolos.news.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Aiolos
 * @date 2020/10/14 10:54 下午
 */
@Getter
@AllArgsConstructor
public enum Sex {

    WOMAN(0 ,"女"),
    MAN(1, "男"),
    SECRET(2, "保密");

    private final Integer type;
    private final String value;
}
