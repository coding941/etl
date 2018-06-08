package com.hisense.etl.dao.impl;

import com.hisense.etl.bean.AttriBaseBean;
import com.hisense.etl.bean.StudentCollegeAttrMetaData;
import com.hisense.etl.dao.BaseDao4Es;
import com.hisense.etl.util.ESConnectUtil;
import com.hisense.etl.util.EsAdminUtil;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDao4EsImpl<T> implements BaseDao4Es<T> {

    private Class clazz;
    private static final Logger logger = LoggerFactory.getLogger(BaseDao4EsImpl.class);
    protected String indexName;
    protected String typeName;

    public BaseDao4EsImpl(){
        super();
    }
    public BaseDao4EsImpl(String indexName,String typeName){
        this.indexName=indexName;
        this.typeName=typeName;
    }


    public boolean saveFromMap(final Map<String,Object> map){
        if(map==null) return false;
        if(ESConnectUtil.getClient()==null) return false;
        try {
            IndexResponse response = ESConnectUtil.getClient().prepareIndex(indexName, typeName).setSource(map).execute().actionGet();
//            logger.info("response id is "+response.getId());
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



    private Map<String ,Object> readMapFromDatabase(){
        Map<String ,Object> map=new HashMap<String ,Object>();

        return map;
    }

    /**
     * 判断指定的索引是否存在
     *
     * @param indexName
     *            索引名
     * @return true/false
     */
    public boolean isCreated(String indexName) {
        IndicesExistsResponse response = ESConnectUtil.getClient()
                .admin()
                .indices()
                .exists(new IndicesExistsRequest()
                        .indices(new String[] { indexName })).actionGet();
        return response.isExists();
    }
    /**
     * type exists
     *
     * @param indexName
     *            索引名
     * @param typeName
     *            索引类型
     * @return true/false
     */
    public boolean isExistsType(String indexName, String typeName) {
        TypesExistsResponse response = ESConnectUtil.getClient()
                .admin()
                .indices()
                .typesExists(
                        new TypesExistsRequest(new String[] { indexName },
                                typeName)).actionGet();
        return response.isExists();
    }
    /**
     * 删除索引
     *
     * @param indexName
     *            索引名
     * @return
     */
    public boolean deleteIndex(String indexName) {
        DeleteIndexResponse response = ESConnectUtil.getClient().admin()
                .indices()
                .prepareDelete(indexName).execute().actionGet();
        return response.isAcknowledged();
    }

    /**
     * create index
     * @param list
     */
    public void createIndex(List<AttriBaseBean> list){
//        try {
        StringBuffer prop=new StringBuffer("");
        for(int i=0;i<list.size();i++){
            prop.append("    \""+list.get(i).getItemEngName()+"\": {");
            prop.append("      \"type\": \""+String.valueOf(list.get(i).getItemDataType())+"\"");
            prop.append("    },");
//            prop.append(list.get(i).getItemEngName()).field("type", String.valueOf(list.get(i).getItemDataType())).endObject();
        }
        if(prop.length()>0)prop.deleteCharAt(prop.length()-1);
//        create index according to mapping that excel defined.
        if(isCreated(indexName))deleteIndex(indexName);
        ESConnectUtil.getClient().admin().indices().prepareCreate(indexName).get();

        ESConnectUtil.getClient().admin().indices().preparePutMapping(indexName)
                .setType(typeName)
                .setSource("{" +
                        "  \"properties\": {" +prop.toString()+
                        "  }" +
                        "}")
                .get();

    }
    /**
     * 关闭索引
     *
     * @param index
     * @return
     */
    public boolean closeIndex(String index) {
        CloseIndexResponse response = ESConnectUtil.getClient().admin()
                .indices().prepareClose(index)
                .get();
        return response.isAcknowledged();
    }

    /**
     * 打开索引
     *
     * @param index
     * @return
     */
    public boolean openIndex(String index) {
        OpenIndexResponse response = ESConnectUtil.getClient().admin()
                .indices().prepareOpen(index)
                .get();
        return response.isAcknowledged();
    }
}
