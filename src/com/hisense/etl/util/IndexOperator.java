package com.hisense.etl.util;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexOperator {
    private static final org.slf4j.Logger logger= LoggerFactory.getLogger(Class.class);
    private static final String CLUSTER_NAME="ESCluster";
    private static final String SERVER_IPS="172.22.16.222";
    private static final String INDEX_NAME="owner";
    private static final String TYPE_NAME="info";

    public void getClusterInfo() {
        List<DiscoveryNode> nodes = ESConnectUtil.getClient().connectedNodes();
        for (DiscoveryNode node : nodes) {
            logger.info(node.getHostAddress());
        }
    }

    public void saveData() {

        try {
//            List<Map<String, Object>> list=new ArrayList<Map<String, Object>>(64);
            Map<String, Object> source = new HashMap<String, Object>();
//            source.put("ownerNo", "2-101");
            source.put("ownerName", "张裕");
//            source.put("houseArea", "97.56");
            source.put("ownerPhone", "13780682695");
//            source.put("ownerPhone2", "13780682578");

            IndexResponse response = ESConnectUtil.getClient().prepareIndex(INDEX_NAME, TYPE_NAME).setSource(source).execute().actionGet();
            logger.info("response id is "+response.getId());
//            logger.info("response version is "+response.getVersion());

        } catch (Exception e) {
            logger.error("保存数据失败！", e);
        }
    }

    public void saveData4Twitter() {

        try {
//            List<Map<String, Object>> list=new ArrayList<Map<String, Object>>(64);
            Map<String, Object> source = new HashMap<String, Object>();
//            source.put("ownerNo", "2-101");
            source.put("user", "zhang与");
            source.put("houseArea", "97.56");
            source.put("postDate", "2013-02-03");
            source.put("message", "out message");

            IndexResponse response = ESConnectUtil.getClient().prepareIndex("twitter", "info").setSource(source).execute().actionGet();
            logger.info("response id is "+response.getId());
//            logger.info("response version is "+response.getVersion());

        } catch (Exception e) {
            logger.error("保存数据失败！", e);
        }
    }
    public void saveFromMap(Map<String,Object> ownerMap) {
        try {
            IndexResponse response = ESConnectUtil.getClient().prepareIndex(INDEX_NAME, TYPE_NAME).setSource(ownerMap).execute().actionGet();
            logger.info("response id is "+response.getId());
//            logger.info("response version is "+response.getVersion());

        } catch (Exception e) {
            logger.error("保存数据失败！", e);
        }
    }

    public void batchSaveFromFile(TransportClient client, File file) {
        try {
            if(!file.exists())
                throw new FileNotFoundException("file does not exist."+file.getName());
            BufferedReader bufferedReader =null;
            bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String data=null;
            String[] attributes;
            Map<String, Object> ownerMap = new HashMap<String, Object>();
            IndexResponse response;
            int rowId=1;
            while ((data=bufferedReader.readLine())!=null){
                attributes=data.split(",");
                for(int i=0;i<attributes.length;i++){
                    switch (i){
                        case 0:
                            ownerMap.put("unitHouseNum",attributes[i]);
                            break;
                        case 1:
                            ownerMap.put("ownerName",attributes[i]);
                            break;
                        case 2:
                            ownerMap.put("taskId",attributes[i]);
                            break;
                        case 3:
                            ownerMap.put("ownerPhone",attributes[i]);
                            break;
                        case 4:
                            ownerMap.put("propertyPhone",attributes[i]);
                            break;
                            default:
                                break;
                    }

                }
//                ownerMap.put("importTime",new FieldStats.Date());
                response= ESConnectUtil.getClient().prepareIndex(INDEX_NAME, TYPE_NAME).setId(""+rowId).setSource(ownerMap).execute().actionGet();
                rowId++;
                logger.info("response id is "+response.getId());
            }


        } catch (Exception e) {
            logger.error("保存数据失败！", e);
        }
    }
    public void deleteById(String id) {
        try {
            DeleteResponse response = ESConnectUtil.getClient().prepareDelete(INDEX_NAME, TYPE_NAME, id)
                    .execute().actionGet();
            logger.info(response.getId()+","+response.getVersion());
        } catch (Exception e) {
            logger.error("删除数据失败！", e);
        }
    }
//    public void deleteType(String indexName,String typeName){
//        StringBuilder b = new StringBuilder();
//        b.append("{\"query\":{\"match_all\":{}}}");
//        DeleteByQueryRequestBuilder response = new DeleteByQueryRequestBuilder(ESConnectUtil.getClient(), DeleteByQueryAction.INSTANCE);
//        response.setIndices(indexName).setTypes(typeName).setSource(b.toString()).execute().actionGet();
//    }

    public void createIndex(String index){
        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";

        IndexResponse response = ESConnectUtil.getClient().prepareIndex(index, "info")
                .setSource(json, XContentType.JSON)
                .get();
    }

    public void deleteByQuery(String indexName,String typeName){

        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(ESConnectUtil.getClient())
//                        .filter(QueryBuilders.matchQuery("fileExten", "xls"))
                        .source(indexName)
                        .get();

        long deleted = response.getDeleted();
//        DeleteByQueryAction.INSTANCE.newRequestBuilder(ESConnectUtil.getClient())
//                .filter(QueryBuilders.matchQuery("fileExten", "xls"))
//                .source(indexName)
//                .execute(new ActionListener<BulkByScrollResponse>() {
//                    @Override
//                    public void onResponse(BulkByScrollResponse response) {
//                        long deleted = response.getDeleted();
//                    }
//                    @Override
//                    public void onFailure(Exception e) {
//                        e.printStackTrace();
//                    }
//                });
    }
    public void testUpdate() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("twitter");
        updateRequest.type("info");
        updateRequest.id("AWMqlDMQppxe9exuZfr0");
        updateRequest.doc(XContentFactory.jsonBuilder()
                .startObject()
                .field("user", "user")
                .field("houseArea", "98.56")
                .endObject());
        UpdateResponse response = ESConnectUtil.getClient().update(updateRequest).get();

        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        long version = response.getVersion();
        logger.info(index + " : " + type + ": " + id + ": " + version);
    }

    /**
     * bulk 批量执行
     * 一次查询可以update 或 delete多个document
     */
    public void testBulk() throws Exception {
        TransportClient client = ESConnectUtil.getClient() ;
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(client.prepareIndex("twitter", "tweet", "1")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
//                        .field("postDate", new FieldStats.Date())
                        .field("message", "trying out Elasticsearch")
                        .endObject()));
        bulkRequest.add(client.prepareIndex("twitter", "tweet", "2")
                .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
//                        .field("postDate", new FieldStats.Date())
                        .field("message", "another post")
                        .endObject()));
        BulkResponse response = bulkRequest.get();
        logger.info("items:"+response.getItems());
    }
    public void queryById(String id){
        GetResponse response = ESConnectUtil.getClient().prepareGet(INDEX_NAME, TYPE_NAME, id).execute().actionGet();
        logger.info(response.getSource().toString());
    }

    public static void main(String[] ar) throws Exception{
        IndexOperator obj = new IndexOperator();
//        obj.createIndex("twitter");
//        obj.saveData4Twitter();
        obj.testUpdate();
//        obj.deleteByQuery("twitter","info");
//        ClientHelper.getInstance().getClient("CLUSTER_NAME");
//        File destFile=new File("D:\\fileRepository\\file_list.txt");
//        obj.batchSaveFromFile(ESConnectUtil.getClient(),destFile);
//        obj.saveData();
//        obj.queryById("1");
//
//        for(int i=1;i<=3;i++)
//            obj.deleteById(""+i);

//        try {
//
//            //设置集群名称
//
//            Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
//            //创建client
////            @SuppressWarnings("resource")
//            TransportClient client = new PreBuiltTransportClient(settings)
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddresses.forString(SERVER_IPS), 9300));
//            //写入数据
////             saveData(client);
//            //搜索数据
//            GetResponse response = ESConnectUtil.getClient(CLUSTER_NAME,SERVER_IPS).prepareGet(INDEX_NAME, TYPE_NAME, "3c1e133e-7da5-4525-892d-8ebbb0b321d8").execute().actionGet();
////            GetResponse response = client.prepareGet(INDEX_NAME, TYPE_NAME, "3c1e133e-7da5-4525-892d-8ebbb0b321d8").execute().actionGet();
//            //输出结果
//            logger.info(response.getSource());
//
//            //关闭client
//            client.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


}
