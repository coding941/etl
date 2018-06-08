package com.hisense.etl.util;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.network.InetAddresses;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ES连接client
 *
 * @author leixun
 * @version 2017年9月25日
 * @see ESConnectUtil
 * @since
 */
public class ESConnectUtil {

    private static final Logger logger= LoggerFactory.getLogger(ESConnectUtil.class);
    private static TransportClient client;

    static{
        try {
            Settings setting = Settings.builder().put("cluster.name", AppConstants.CLUSTER_NAME)
                    .put("client.transport.sniff", true).build();
            client = new PreBuiltTransportClient(setting);

            if (AppConstants.CLUSTER_SERVER_IPS.contains(",")) {
                String[] serverStr = AppConstants.CLUSTER_SERVER_IPS.split(",");
                for (int i = 0; i < serverStr.length; i++) {
                    TransportAddress transportAddress = new InetSocketTransportAddress(
                            InetAddresses.forString(serverStr[i]), 9300);
                    client.addTransportAddresses(transportAddress);
                }
            } else {
                TransportAddress transportAddress = new InetSocketTransportAddress(InetAddresses.forString(AppConstants.CLUSTER_SERVER_IPS),
                        9300);
                client.addTransportAddresses(transportAddress);
            }
        } catch (Exception e) {
            logger.info("获取esClient失败~!");
        }
    }

    /**
     *
     * Description: getClient工具类<br>
     *
     * @return
     * @see
     */
    public static TransportClient getClient() {
        return client;
    }
}

