package com.hisense.etl.util;

import com.hisense.etl.bean.StudentCollegeAttrMetaData;
import com.hisense.etl.bean.StudentCollegeContMetaData;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsAdminUtil {

    private static final Logger LOG = LoggerFactory.getLogger(EsAdminUtil.class);
    private String indexName;
    private String typeName;

    public EsAdminUtil(String indexName,String typeName){
        this.indexName=indexName;
        this.typeName=typeName;
    }

    public void createIndex(List<StudentCollegeAttrMetaData> list){
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
//                        "    \"name\": {" +
//                        "      \"type\": \"text\"" +
//                        "    }," +
//                        "    \"usedName\": {" +
//                                "      \"type\": \"keyword\"" +
//                                "    }"+
                        "  }" +
                        "}")
                .get();

//            ESConnectUtil.getClient().admin().indices().prepareCreate(indexName).execute().actionGet();
//
//            XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(indexName).startObject("properties");
////            for(int i=0;i<list.size();i++){
////                builder.startObject(list.get(i).getItemEngName()).field("type", String.valueOf(list.get(i).getItemDataType())).endObject();
////            }
//            builder.startObject("id").field("type", "integer").field("store", "yes").endObject()
//                    .startObject("kw").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject();
//            builder.endObject().endObject().endObject();
//
//
//            PutMappingRequest mapping = Requests.putMappingRequest(indexName).type("info").source(builder);
//            ESConnectUtil.getClient().admin().indices().putMapping(mapping).actionGet();


//            ESConnectUtil.getClient().admin().indices().prepareCreate(indexName).addMapping("info",builder).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    public static void main(String[] ar){
        //create index from excel
//        EsAdminUtil.createIndexFromFile(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\数据说明\\高校学生信息.xlsx"));
//        import data from excel
//        parseFileListFromExcel(new File("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\数据说明\\高校学生信息.xlsx"));

    }
}
