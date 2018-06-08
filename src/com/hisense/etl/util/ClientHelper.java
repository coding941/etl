package com.hisense.etl.util;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.network.InetAddresses;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tgg on 16-3-17.
 */
public class ClientHelper {

    private Settings setting;

    private Map<String, Client> clientMap = new ConcurrentHashMap<String, Client>();

    private Map<String, Integer> ips = new HashMap<String, Integer>(); // ip_address. port

    private final String clusterName = "ESCluster";

    private ClientHelper() {
        init();

    }

    public static final ClientHelper getInstance() {
        return ClientHolder.INSTANCE;
    }

    private static class ClientHolder {
        private static final ClientHelper INSTANCE = new ClientHelper();
    }

    /**
     * 初始化默认的client
     */
    public void init() {
        ips.put("172.22.16.220", 9300);
        ips.put("172.22.16.221", 9300);
        ips.put("172.22.16.222", 9300);
        setting = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", true).build();
        addClient(setting, getAllAddress(ips));
    }

    /**
     * 获得所有的地址端口
     *
     * @return
     */
    public List<InetSocketTransportAddress> getAllAddress(Map<String, Integer> ips) {
        List<InetSocketTransportAddress> addressList = new ArrayList<InetSocketTransportAddress>();
        for (String ip : ips.keySet()) {
            addressList.add(new InetSocketTransportAddress(InetAddresses.forString(ip), ips.get(ip)));
        }
        return addressList;
    }

    public Client getClient() {
        return getClient(clusterName);
    }

    public Client getClient(String clusterName) {
        return clientMap.get(clusterName);
    }

    public void addClient(Settings setting, List<InetSocketTransportAddress> transportAddress) {
        Client client = new PreBuiltTransportClient(setting)
                .addTransportAddresses(transportAddress
                        .toArray(new InetSocketTransportAddress[transportAddress.size()]));
        clientMap.put(setting.get("cluster.name"), client);
    }
}
