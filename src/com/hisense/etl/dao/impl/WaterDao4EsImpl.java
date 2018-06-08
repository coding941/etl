package com.hisense.etl.dao.impl;

import com.hisense.etl.bean.StudentCollegeAttrMetaData;
import com.hisense.etl.bean.WaterAttrMetaData;
import com.hisense.etl.dao.BaseDao4Es;
import com.hisense.etl.util.ESConnectUtil;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class WaterDao4EsImpl<T> extends BaseDao4EsImpl<T> {

    private Class clazz;
    private static final Logger logger = LoggerFactory.getLogger(WaterDao4EsImpl.class);

    public WaterDao4EsImpl(String indexName,String typeName){
        this.indexName=indexName;
        this.typeName=typeName;
    }


}
