package com.aiolos.news.common.response;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aiolos
 * @date 2021/3/15 11:01 上午
 */
@Getter
@ToString
abstract class AbstractCommonResponse {

    private Map<Object, Object> map;

    AbstractCommonResponse() {}

    public AbstractCommonResponse init(int capacity) {
        map = new HashMap<>(capacity);
        return this;
    }

    public AbstractCommonResponse put(Object key, Object value) {
        map.put(key, value);
        return this;
    }
}