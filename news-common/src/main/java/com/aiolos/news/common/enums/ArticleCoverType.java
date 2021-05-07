package com.aiolos.news.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Aiolos
 * @date 2020/11/26 6:32 上午
 */
@Getter
@AllArgsConstructor
public enum ArticleCoverType {

    ONE_IMAGE(1, "单图"),
    WORDS(2, "纯文字");

    private final Integer type;
    private final String value;
}