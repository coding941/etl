package com.hisense.etl.dao.impl;

import com.hisense.etl.dao.BaseDao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BaseDaoImpl<T> implements BaseDao<T>{

    private Class clazz;

    public BaseDaoImpl(){
        Class clazz = this.getClass();
        Type type = clazz.getGenericSuperclass();
        ParameterizedType ptype=(ParameterizedType)type;
//        arguments in runtime environment
        Type[] types = ptype.getActualTypeArguments();
        Class clazzParameter=(Class)types[0];
        this.clazz=clazzParameter;
    }

    public void add(T t){

    }
    public boolean delete(T t){
        return true;
    }
    public boolean update(T t){
        return true;
    }
    public T findById(Serializable id){
        return (T)new Object();
    }
    public List<T> findAll(){
        List<T> list=new ArrayList<T>();
        return list;
    }
}
