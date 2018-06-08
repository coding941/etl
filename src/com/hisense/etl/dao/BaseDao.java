package com.hisense.etl.dao;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<T> {

    void add(T t);
    boolean delete(T t);
    boolean update(T t);
    T findById(Serializable id);
    List<T> findAll();

}
