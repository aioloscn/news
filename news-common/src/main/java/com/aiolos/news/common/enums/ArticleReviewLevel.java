package com.aiolos.news.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Aiolos
 * @date 2021/5/15 4:57 上午
 */
@Getter
@AllArgsConstructor
public enum ArticleReviewLevel {

    PASS("pass", "自动审核通过"),
    REVIEW("review", "建议人工复审"),
    BLOCK("block", "自动审核不通过");

    private String type;
    private String value;
}
