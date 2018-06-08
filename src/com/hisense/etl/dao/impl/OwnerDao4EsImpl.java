package com.hisense.etl.dao.impl;

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

import java.util.Map;

public class OwnerDao4EsImpl<T> implements BaseDao4Es<T> {

    private Class clazz;
    private static final Logger logger = LoggerFactory.getLogger(OwnerDao4EsImpl.class);
    protected String indexName="owner";
    protected String typeName="info";


    public OwnerDao4EsImpl(){
//        Class clazz = this.getClass();
//        Type type = clazz.getGenericSuperclass();
//        ParameterizedType ptype=(ParameterizedType)type;
//        Type[] types = ptype.getActualTypeArguments();
//        Class clazzParameter=(Class)types[0];
//        this.clazz=clazzParameter;
    }

    public boolean saveFromMap(Map<String,Object> ownerMap){
        try {
            IndexResponse response = ESConnectUtil.getClient().prepareIndex(indexName, typeName).setSource(ownerMap).execute().actionGet();
            logger.info("response id is "+response.getId());
            return true;
//            logger.info("response version is "+response.getVersion());

        } catch (Exception e) {
            logger.error("保存数据失败！", e);
            return false;
        }
    }
    public boolean saveFromBean(T t){
        try {
//            List<Map<String, Object>> list=new ArrayList<Map<String, Object>>(64);
//            Map<String, Object> source = new HashMap<String, Object>();
////            source.put("ownerNo", "2-101");
//            source.put("ownerName", "张裕");
////            source.put("houseArea", "97.56");
//            source.put("ownerPhone", "13780682695");
////            source.put("ownerPhone2", "13780682578");
//            return saveFromMap(t.toMap());
            return true;

        } catch (Exception e) {
            logger.error("保存数据失败！", e);
            return false;
        }
    }
    public void deleteById(String id){
        try {
            DeleteResponse response = ESConnectUtil.getClient().prepareDelete(indexName, typeName, id)
                    .execute().actionGet();
            logger.info(response.getId()+","+response.getVersion());
        } catch (Exception e) {
            logger.error("删除数据失败！", e);
        }
    }

    public void deleteByQuery(QueryBuilders queryBuilders){
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(ESConnectUtil.getClient())
                        .filter(QueryBuilders.matchQuery("fileExten", "xls"))
                        .source(indexName)
                        .get();

        long deleted = response.getDeleted();
        DeleteByQueryAction.INSTANCE.newRequestBuilder(ESConnectUtil.getClient())
                .filter(QueryBuilders.matchQuery("fileExten", "xls"))
                .source(indexName)
                .execute(new ActionListener<BulkByScrollResponse>() {
                    @Override
                    public void onResponse(BulkByScrollResponse response) {
                        long deleted = response.getDeleted();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
