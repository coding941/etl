package com.hisense.etl.dao;

import org.elasticsearch.index.query.QueryBuilders;

import java.util.Map;

public interface BaseDao4Es<T> {



    boolean saveFromMap(Map<String, Object> ownerMap);
    boolean saveFromBean(T t);
    void deleteById(String id);
    void deleteByQuery(QueryBuilders queryBuilders);


}
