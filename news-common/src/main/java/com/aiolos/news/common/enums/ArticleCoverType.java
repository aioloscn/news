package com.aiolos.news.common.enums;

/**
 * @author Aiolos
 * @date 2020/11/26 6:32 上午
 */
public enum ArticleCoverType {

    ONE_IMAGE(1, "单图"),
    WORDS(2, "纯文字");

    public final Integer type;
    public final String value;

    ArticleCoverType(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
